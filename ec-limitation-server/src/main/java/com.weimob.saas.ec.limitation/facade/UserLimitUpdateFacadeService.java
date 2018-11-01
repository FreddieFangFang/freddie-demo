package com.weimob.saas.ec.limitation.facade;

import com.alibaba.dubbo.rpc.RpcContext;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
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
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.saas.ec.limitation.utils.LimitationRedisClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
       //TODO 先查询changelog 有无记录  有：直接回滚， 没有  push
        try {
            reverseUserLimitHandler.reverse(requestVo.getTicket());
        } catch (Exception e){
            // 回滚用户限购 先将限购ticket写入redis队列中(压测和正常设置不同的key)
            if (RpcContext.getContext().getGlobalTicket() != null && RpcContext.getContext().getGlobalTicket().startsWith("EC_STRESS-")) {
                LimitationRedisClientUtils.pushDataToQueue(LimitConstant.EC_STRESS_KEY_LIMITATION_REVERSE_QUEUE,requestVo.getTicket());
            } else {
                LimitationRedisClientUtils.pushDataToQueue(LimitConstant.KEY_LIMITATION_REVERSE_QUEUE,requestVo.getTicket());
            }
        }
        return new ReverseUserLimitResponseVo(true);
    }

}
