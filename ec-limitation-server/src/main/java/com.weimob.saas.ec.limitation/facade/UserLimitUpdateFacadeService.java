package com.weimob.saas.ec.limitation.facade;

import com.weimob.saas.ec.common.request.MergeWidRequest;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.handler.biz.DeductUserLimitHandler;
import com.weimob.saas.ec.limitation.handler.biz.ReverseUserLimitHandler;
import com.weimob.saas.ec.limitation.handler.biz.SaveUserLimitHandler;
import com.weimob.saas.ec.limitation.model.LimitBo;
import com.weimob.saas.ec.limitation.model.request.DeductUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.ReverseUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.SaveUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.model.response.ReverseUserLimitResponseVo;
import com.weimob.saas.ec.limitation.model.response.UpdateUserLimitResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private SaveUserLimitHandler saveUserLimitHandler;
    @Autowired
    private DeductUserLimitHandler deductUserLimitHandler;
    @Autowired
    private ReverseUserLimitHandler reverseUserLimitHandler;
    @Autowired
    private LimitationServiceImpl limitationService;

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

        LimitContext.setLimitBo(new LimitBo());
        reverseUserLimitHandler.reverse(requestVo.getTicket());

        return new ReverseUserLimitResponseVo(true);
    }

    public boolean mergeLimitByWid(MergeWidRequest mergeWidRequest){
        Long pid = mergeWidRequest.getPid();
        Long newWid = mergeWidRequest.getNewWid();
        Long oldWid = mergeWidRequest.getOldWid();
        //1.合并 user-limit
        mergeUserLimit(pid,newWid, oldWid);


        //2.合并 user-goods-limit
        mergeUserGoodsLimit(pid,newWid, oldWid);

        //wid2累加到wid1上面，然后wid2删除
        //回滚操作？ 是指wid2并到wid1后，发现问题再回退这个合并操作？？
        //如果合并的时候，日志表发生了回滚，会导致wid2来回滚发现没有wid2了，回滚失败，现在暂时不管了
        //单个合并  还是  一键后台合并
        // wid1 和wid2 之间的联系，我们怎么获得
        return true;
    }

    private void mergeUserLimit(Long pid, Long newWid, Long oldWid) {
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
        //wid2中剩余的
        for(Map.Entry<String,UserLimitEntity> entry:oldLimitMap.entrySet()){
            saveUserLimit.add(entry.getValue());
        }
        limitationService.updateUserLimitList(updateUserLimit);
        limitationService.saveUserLimitList(saveUserLimit);
        limitationService.deleteUserLimitList(oldLimit);
    }

    private void mergeUserGoodsLimit(Long pid, Long newWid, Long oldWid) {
        List<UserGoodsLimitEntity> newLimit = limitationService.getUserGoodsLimitList(pid,newWid);
        Map<String,UserGoodsLimitEntity> newLimitMap = new HashMap<>();
        for(UserGoodsLimitEntity userGoodsLimitEntity :newLimit){
            String LimitKey = userGoodsLimitEntity.getPid()+"_"+userGoodsLimitEntity.getStoreId()+"_"+userGoodsLimitEntity.getLimitId();
            newLimitMap.put(LimitKey,userGoodsLimitEntity);
        }

        //查询wid2
        List<UserGoodsLimitEntity> oldLimit = limitationService.getUserGoodsLimitList(pid,oldWid);
        Map<String,UserGoodsLimitEntity> oldLimitMap = new HashMap<>();
        for(UserGoodsLimitEntity userGoodsLimitEntity :oldLimit){
            String LimitKey = userGoodsLimitEntity.getPid()+"_"+userGoodsLimitEntity.getStoreId()+"_"+userGoodsLimitEntity.getLimitId();
            newLimitMap.put(LimitKey,userGoodsLimitEntity);
        }

        for(Map.Entry<String,UserGoodsLimitEntity> entry:newLimitMap.entrySet()){
            if(oldLimitMap.get(entry.getKey())!=null){
                UserGoodsLimitEntity newUserLimitEntity = entry.getValue();
                UserGoodsLimitEntity oldUserLimitEntity = newLimitMap.get(entry.getKey());
                newUserLimitEntity.setBuyNum(newUserLimitEntity.getBuyNum()+oldUserLimitEntity.getBuyNum());
                oldLimitMap.remove(entry.getKey());//从wid2中移除
            }
        }
        List<UserGoodsLimitEntity> updateUserGoodsLimit = new ArrayList<>();
        List<UserGoodsLimitEntity> saveUserGoodsLimit = new ArrayList<>();
        for(Map.Entry<String,UserGoodsLimitEntity> entry:newLimitMap.entrySet()){
            updateUserGoodsLimit.add(entry.getValue());
        }
        //wid2中剩余的
        for(Map.Entry<String,UserGoodsLimitEntity> entry:oldLimitMap.entrySet()){
            saveUserGoodsLimit.add(entry.getValue());
        }
        limitationService.updateUserGoodsLimitList(updateUserGoodsLimit);
        limitationService.saveUserGoodsLimitList(saveUserGoodsLimit);
        limitationService.deleteUserGoodsLimitList(oldLimit);
    }
}
