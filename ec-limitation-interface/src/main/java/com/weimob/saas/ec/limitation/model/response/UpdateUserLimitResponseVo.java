package com.weimob.saas.ec.limitation.model.response;

import java.io.Serializable;

/**
 * @author lujialin
 * @description 减少、增加限购记录出参
 * @date 2018/6/5 17:34
 */
public class UpdateUserLimitResponseVo implements Serializable {
    private static final long serialVersionUID = 8172766852607946305L;

    /**
     * 唯一的凭证
     */
    private String ticket;

    public UpdateUserLimitResponseVo(String ticket) {
        this.ticket = ticket;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
