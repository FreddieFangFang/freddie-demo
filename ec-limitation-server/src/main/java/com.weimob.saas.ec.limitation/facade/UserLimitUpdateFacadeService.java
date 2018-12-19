package com.weimob.saas.ec.limitation.facade;

import com.weimob.saas.ec.common.request.MergeWidRequest;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.biz.DeductUserLimitHandler;
import com.weimob.saas.ec.limitation.handler.biz.ReverseUserLimitHandler;
import com.weimob.saas.ec.limitation.handler.biz.SaveUserLimitHandler;
import com.weimob.saas.ec.limitation.model.LimitBo;
import com.weimob.saas.ec.limitation.model.request.DeductUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.ReverseUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.SaveUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.response.ReverseUserLimitResponseVo;
import com.weimob.saas.ec.limitation.model.response.UpdateUserLimitResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import org.apache.commons.collections.CollectionUtils;
import com.weimob.saas.ec.limitation.utils.LimitationRedisClientUtils;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lujialin
 * @description 用户限购facade层
 * @date 2018/6/5 18:20
 */
@Service(value = "userLimitUpdateFacadeService")
public class UserLimitUpdateFacadeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLimitUpdateFacadeService.class);

    @Autowired
    private SaveUserLimitHandler saveUserLimitHandler;
    @Autowired
    private DeductUserLimitHandler deductUserLimitHandler;
    @Autowired
    private ReverseUserLimitHandler reverseUserLimitHandler;
    @Autowired
    private LimitationServiceImpl limitationService;
    @Resource(name = "mergeLimitByWid")
    private ThreadPoolTaskExecutor mergeLimitByWidExecutor;


    public UpdateUserLimitResponseVo saveUserLimit(SaveUserLimitRequestVo requestVo) {
        LimitContext.setLimitBo(new LimitBo());

        String ticket = saveUserLimitHandler.doHandler(requestVo.getUpdateUserLimitVoList());

        return new UpdateUserLimitResponseVo(ticket);

    }

    public UpdateUserLimitResponseVo deductUserLimit(DeductUserLimitRequestVo requestVo) {

        LimitContext.setLimitBo(new LimitBo());
        String ticket = deductUserLimitHandler.doHandler(requestVo.getUpdateUserLimitVoList());

        return new UpdateUserLimitResponseVo(ticket);
    }

    public ReverseUserLimitResponseVo reverseUserLimit(ReverseUserLimitRequestVo requestVo) {

        VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo);
        VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo.getTicket());

        try {
            reverseUserLimitHandler.reverse(requestVo.getTicket());
        } catch (Exception e){
            LimitationRedisClientUtils.pushDataToQueue(LimitConstant.KEY_LIMITATION_REVERSE_QUEUE,requestVo.getTicket());
        }
        return new ReverseUserLimitResponseVo(true);
    }

    public boolean mergeLimitByWid(MergeWidRequest mergeWidRequest) {
        final Long pid = mergeWidRequest.getPid();
        final Long newWid = mergeWidRequest.getNewWid();
        final Long oldWid = mergeWidRequest.getOldWid();
        mergeLimitByWidExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //1.合并 user-limit
                    mergeUserLimit(pid, newWid, oldWid);
                    //2.合并 user-goods-limit
                    mergeUserGoodsLimit(pid, newWid, oldWid);
                } catch (Exception e) {
                    LOGGER.error("mergeUserLimit异常...");
                    String value = pid + "_" + newWid + "_" + oldWid+"_"+System.currentTimeMillis();
                    LimitationRedisClientUtils.pushDataToQueue(LimitConstant.KEY_LIMITATION_WIDMERGE_QUEUE, value);
                }
            }
        });


        return true;
    }

    public void mergeUserLimit(Long pid, Long newWid, Long oldWid) {
        //查出wid1
        List<UserLimitEntity> newLimit = limitationService.getUserLimitList(pid, newWid);
        Map<String,UserLimitEntity> newLimitMap = new HashMap<>();
        for(UserLimitEntity userLimitEntity :newLimit){
            String LimitKey = userLimitEntity.getPid()+"_"+userLimitEntity.getStoreId()+"_"+userLimitEntity.getLimitId();
            newLimitMap.put(LimitKey,userLimitEntity);
        }

        //查询wid2
        List<UserLimitEntity> oldLimit = limitationService.getUserLimitList(pid, oldWid);
        Map<String,UserLimitEntity> oldLimitMap = new HashMap<>();
        for(UserLimitEntity userLimitEntity :oldLimit){
            String LimitKey = userLimitEntity.getPid()+"_"+userLimitEntity.getStoreId()+"_"+userLimitEntity.getLimitId();
            oldLimitMap.put(LimitKey,userLimitEntity);
        }

        for(Map.Entry<String,UserLimitEntity> entry:newLimitMap.entrySet()){
            if(oldLimitMap.get(entry.getKey())!=null){
                UserLimitEntity newUserLimitEntity = entry.getValue();
                UserLimitEntity oldUserLimitEntity = oldLimitMap.get(entry.getKey());
                newUserLimitEntity.setBuyNum(newUserLimitEntity.getBuyNum()+oldUserLimitEntity.getBuyNum());
                oldLimitMap.remove(entry.getKey());//从wid2中移除
            }
        }
        List<UserLimitEntity> updateUserLimit = new ArrayList<>();
        List<UserLimitEntity> saveUserLimit = new ArrayList<>();
        for(Map.Entry<String,UserLimitEntity> entry:newLimitMap.entrySet()){
            updateUserLimit.add(entry.getValue());
        }
        //wid2中剩余的，wid设置成wid1的值
        for(Map.Entry<String,UserLimitEntity> entry:oldLimitMap.entrySet()){
            UserLimitEntity userLimitEntity = entry.getValue();
            UserLimitEntity newUserLimitEntity = new UserLimitEntity();
            newUserLimitEntity.setPid(userLimitEntity.getPid());
            newUserLimitEntity.setBuyNum(userLimitEntity.getBuyNum());
            newUserLimitEntity.setStoreId(userLimitEntity.getStoreId());
            newUserLimitEntity.setLimitId(userLimitEntity.getLimitId());
            newUserLimitEntity.setBizId(userLimitEntity.getBizId());
            newUserLimitEntity.setBizType(userLimitEntity.getBizType());
            newUserLimitEntity.setWid(newWid);
            saveUserLimit.add(newUserLimitEntity);
        }
        if(CollectionUtils.isNotEmpty(updateUserLimit)){
            limitationService.updateUserLimitList(updateUserLimit);
        }
        if(CollectionUtils.isNotEmpty(saveUserLimit)){
            limitationService.saveUserLimitList(saveUserLimit);
        }
        if(CollectionUtils.isNotEmpty(oldLimit)){
            limitationService.deleteUserLimitList(oldLimit);
        }
    }

    public void mergeUserGoodsLimit(Long pid, Long newWid, Long oldWid) {
        List<UserGoodsLimitEntity> newLimit = limitationService.getUserGoodsLimitList(pid,newWid);
        Map<String,UserGoodsLimitEntity> newLimitMap = new HashMap<>();
        for(UserGoodsLimitEntity userGoodsLimitEntity :newLimit){
            String LimitKey = userGoodsLimitEntity.getPid()+"_"+userGoodsLimitEntity.getStoreId()
                    +"_"+userGoodsLimitEntity.getLimitId()+"_"+userGoodsLimitEntity.getGoodsId();
            newLimitMap.put(LimitKey,userGoodsLimitEntity);
        }

        //查询wid2
        List<UserGoodsLimitEntity> oldLimit = limitationService.getUserGoodsLimitList(pid,oldWid);
        Map<String,UserGoodsLimitEntity> oldLimitMap = new HashMap<>();
        for(UserGoodsLimitEntity userGoodsLimitEntity :oldLimit){
            String LimitKey = userGoodsLimitEntity.getPid()+"_"+userGoodsLimitEntity.getStoreId()
                    +"_"+userGoodsLimitEntity.getLimitId()+"_"+userGoodsLimitEntity.getGoodsId();
            oldLimitMap.put(LimitKey,userGoodsLimitEntity);
        }

        for(Map.Entry<String,UserGoodsLimitEntity> entry:newLimitMap.entrySet()){
            if(oldLimitMap.get(entry.getKey())!=null){
                UserGoodsLimitEntity newUserLimitEntity = entry.getValue();
                UserGoodsLimitEntity oldUserLimitEntity = oldLimitMap.get(entry.getKey());
                newUserLimitEntity.setBuyNum(newUserLimitEntity.getBuyNum()+oldUserLimitEntity.getBuyNum());
                oldLimitMap.remove(entry.getKey());//从wid2中移除
            }
        }
        List<UserGoodsLimitEntity> updateUserGoodsLimit = new ArrayList<>();
        List<UserGoodsLimitEntity> saveUserGoodsLimit = new ArrayList<>();
        for(Map.Entry<String,UserGoodsLimitEntity> entry:newLimitMap.entrySet()){
            updateUserGoodsLimit.add(entry.getValue());
        }
        //wid2中剩余的，wid设置成wid1的值
        for(Map.Entry<String,UserGoodsLimitEntity> entry:oldLimitMap.entrySet()){
            UserGoodsLimitEntity userGoodsLimitEntity = entry.getValue();
            UserGoodsLimitEntity newUserGoodsLimitEntity = new UserGoodsLimitEntity();
            newUserGoodsLimitEntity.setPid(userGoodsLimitEntity.getPid());
            newUserGoodsLimitEntity.setBuyNum(userGoodsLimitEntity.getBuyNum());
            newUserGoodsLimitEntity.setStoreId(userGoodsLimitEntity.getStoreId());
            newUserGoodsLimitEntity.setLimitId(userGoodsLimitEntity.getLimitId());
            newUserGoodsLimitEntity.setGoodsId(userGoodsLimitEntity.getGoodsId());
            newUserGoodsLimitEntity.setWid(newWid);
            saveUserGoodsLimit.add(newUserGoodsLimitEntity);
        }
        if(CollectionUtils.isNotEmpty(updateUserGoodsLimit)) {
            limitationService.updateUserGoodsLimitList(updateUserGoodsLimit);
        }
        if(CollectionUtils.isNotEmpty(saveUserGoodsLimit)) {
            limitationService.saveUserGoodsLimitList(saveUserGoodsLimit);
        }
        if(CollectionUtils.isNotEmpty(oldLimit)) {
            limitationService.deleteUserGoodsLimitList(oldLimit);
        }
    }
}
