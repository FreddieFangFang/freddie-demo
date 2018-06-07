package com.weimob.saas.ec.limitation.utils;

import com.weimob.saas.ec.limitation.model.LimitBo;

/**
 * @author lujialin
 * @description 限购本地线程变量
 * @date 2018/6/6 14:50
 */
public class LimitContext {

    private static ThreadLocal<String> ticket = new ThreadLocal<>();

    private static ThreadLocal<LimitBo> limitBo = new ThreadLocal<>();

    public static String getTicket() {
        return ticket.get();
    }

    public static LimitBo getLimitBo() {
        return limitBo.get();
    }

    public static void setTicket(String ticket) {
        LimitContext.ticket.set(ticket);
    }

    public static void setLimitBo(LimitBo limitBo) {
        LimitContext.limitBo.set(limitBo);
    }

    public static void clearAll() {
        setTicket(null);
        setLimitBo(null);
    }
}
