package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

/**
 * @author lujialin
 * @description 删除限购信息入参
 * @date 2018/5/30 16:31
 */
public class DeleteLimitationRequestVo extends BaseRequest {

    private static final long serialVersionUID = -6261328855032120000L;

    /**
     * 活动id或者商品id
     */
    private Long bizId;
    /**
     * 活动类型或者商品限购类型
     */
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
