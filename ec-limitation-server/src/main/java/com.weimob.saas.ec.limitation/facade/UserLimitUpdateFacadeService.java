package com.weimob.saas.ec.limitation.facade;

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
        reverseUserLimitHandler.reverse(requestVo.getTicket());

        return new ReverseUserLimitResponseVo(true);
    }
}
