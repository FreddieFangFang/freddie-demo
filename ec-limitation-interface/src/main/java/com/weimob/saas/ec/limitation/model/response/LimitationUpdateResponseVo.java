package com.weimob.saas.ec.limitation.model.response;

import java.io.Serializable;

/**
 * @author lujialin
 * @description 更新限购主表出参
 * @date 2018/5/29 10:37
 */
public class LimitationUpdateResponseVo implements Serializable {

    private static final long serialVersionUID = 815067948059843948L;

    private Long limitId;

    private Boolean status;

    private String ticket;

    public LimitationUpdateResponseVo() {

    }

    public LimitationUpdateResponseVo(Long limitId, Boolean status) {
        this.limitId = limitId;
        this.status = status;
    }

    public LimitationUpdateResponseVo(Long limitId, Boolean status, String ticket) {
        this.limitId = limitId;
        this.status = status;
        this.ticket = ticket;
    }

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
