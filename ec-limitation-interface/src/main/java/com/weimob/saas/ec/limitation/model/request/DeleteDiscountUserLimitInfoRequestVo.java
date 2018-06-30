package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

/**
 * @author lujialin
 * @description 周期性清除限时折扣用户购买记录
 * @date 2018/6/29 14:30
 */
public class DeleteDiscountUserLimitInfoRequestVo extends BaseRequest {

    private Long bizId;

    private Integer bizType;

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }
}
