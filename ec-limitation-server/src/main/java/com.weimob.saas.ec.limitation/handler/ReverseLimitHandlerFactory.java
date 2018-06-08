package com.weimob.saas.ec.limitation.handler;

import com.weimob.saas.ec.limitation.common.LimitServiceNameEnum;
import com.weimob.saas.ec.limitation.handler.biz.DeductUserLimitHandler;
import com.weimob.saas.ec.limitation.handler.biz.SaveUserLimitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Pengqin ZHOU
 * @date 2018/6/8
 */
@Service("reverseLimitHandlerFactory")
public class ReverseLimitHandlerFactory {

    @Autowired
    private SaveUserLimitHandler saveUserLimitHandler;
    @Autowired
    private DeductUserLimitHandler deductUserLimitHandler;

    public Handler<?> getHandlerByServiceName(String serviceName) {

        Handler<?> handler = null;

        if (LimitServiceNameEnum.SAVE_USER_LIMIT.name().equals(serviceName)) {
            handler = saveUserLimitHandler;
        } else if (LimitServiceNameEnum.SAVE_USER_LIMIT.name().equals(serviceName)) {
            handler = deductUserLimitHandler;
        }

        return handler;
    }
}
