package com.weimob.saas.ec.limitation.handler;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.model.LimitBo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author lujialin
 * @description 限购chain
 * @date 2018/6/6 15:23
 */
public class LimitBizChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(LimitBizChain.class);

    protected List<LimitBizHandler> handlers;

    public void execute(List<UpdateUserLimitVo> vos) {
        Long startTime = null;
        for (LimitBizHandler handler : handlers) {
            startTime = System.currentTimeMillis();
            handler.doLimitHandler(vos);
            try {
                Cat.logTransaction("LIMIT_HANDLER", handler.getClass().getSimpleName(), startTime, Message.SUCCESS);
            } catch (Throwable t) {
                LOGGER.error("cat打点异常：", t);
            }
        }
    }

    public void setHandlers(List<LimitBizHandler> handlers) {
        this.handlers = handlers;
    }
}
