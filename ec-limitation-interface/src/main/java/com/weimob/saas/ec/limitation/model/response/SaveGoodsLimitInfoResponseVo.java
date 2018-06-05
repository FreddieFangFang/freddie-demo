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

    private Long limitId;

    public SaveGoodsLimitInfoResponseVo() {

    }

    public SaveGoodsLimitInfoResponseVo(Boolean status, Long limitId) {
        this.status = status;
        this.limitId = limitId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }
}
