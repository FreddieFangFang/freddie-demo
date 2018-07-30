package com.weimob.saas.ec.limitation.handler;

import com.weimob.saas.ec.limitation.common.LimitServiceNameEnum;
import com.weimob.saas.ec.limitation.handler.biz.DeductUserLimitHandler;
import com.weimob.saas.ec.limitation.handler.biz.SaveUserLimitHandler;
import com.weimob.saas.ec.limitation.handler.reverse.ReverseDeleteGoodsLimitHandler;
import com.weimob.saas.ec.limitation.handler.reverse.ReverseDeleteLimitationHandler;
import com.weimob.saas.ec.limitation.handler.reverse.ReverseSaveGoodsLimitHandler;
import com.weimob.saas.ec.limitation.handler.reverse.ReverseUpdateGoodsLimitHandler;
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
    @Autowired
    private ReverseUpdateGoodsLimitHandler reverseUpdateGoodsLimitHandler;
    @Autowired
    private ReverseSaveGoodsLimitHandler reverseSaveGoodsLimitHandler;
    @Autowired
    private ReverseDeleteGoodsLimitHandler reverseDeleteGoodsLimitHandler;
    @Autowired
    private ReverseDeleteLimitationHandler reverseDeleteLimitationHandler;

    public Handler<?> getHandlerByServiceName(String serviceName) {

        Handler<?> handler = null;

        if (LimitServiceNameEnum.SAVE_USER_LIMIT.name().equals(serviceName)) {
            handler = saveUserLimitHandler;
        } else if (LimitServiceNameEnum.SAVE_USER_LIMIT.name().equals(serviceName)) {
            handler = deductUserLimitHandler;
        } else if (LimitServiceNameEnum.UPDATE_GOODS_LIMIT.name().equals(serviceName)) {
            handler = reverseUpdateGoodsLimitHandler;
        } else if (LimitServiceNameEnum.SAVE_GOODS_LIMIT.name().equals(serviceName)) {
            handler = reverseSaveGoodsLimitHandler;
        } else if (LimitServiceNameEnum.DELETE_GOODS_LIMIT.name().equals(serviceName)) {
            handler = reverseDeleteGoodsLimitHandler;
        } else if (LimitServiceNameEnum.DELETE_ACTIVITY_LIMIT.name().equals(serviceName)) {
            handler = reverseDeleteLimitationHandler;
        }

        return handler;
    }
}
