package com.weimob.saas.ec.limitation.model.request;

import java.io.Serializable;

/**
 * @author lujialin
 * @description 回滚限购记录入参
 * @date 2018/6/5 17:48
 */
public class ReverseUserLimitRequestVo implements Serializable {
    private static final long serialVersionUID = 2540649098549215112L;
    
    private String ticket;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
