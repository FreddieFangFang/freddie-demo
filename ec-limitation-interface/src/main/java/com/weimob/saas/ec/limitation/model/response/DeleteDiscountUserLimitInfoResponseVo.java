package com.weimob.saas.ec.limitation.model.response;

import java.io.Serializable;

/**
 * @author lujialin
 * @description 周期性清除限时折扣用户购买记录出参
 * @date 2018/6/29 14:34
 */
public class DeleteDiscountUserLimitInfoResponseVo implements Serializable {

    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}