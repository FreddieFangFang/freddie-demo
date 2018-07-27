package com.weimob.saas.ec.limitation.model.response;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;

/**
 * @author lujialin
 * @description 保存限购商品出参
 * @date 2018/6/4 17:47
 */
public class SaveGoodsLimitInfoResponseVo implements Serializable {

    private static final long serialVersionUID = -6782071824727251332L;

    private Boolean status;

    private String ticket;

    public SaveGoodsLimitInfoResponseVo() {

    }

    public SaveGoodsLimitInfoResponseVo(Boolean status, String ticket) {
        this.status = status;
        this.ticket = ticket;
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
