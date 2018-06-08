package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

/**
 * @author lujialin
 * @description 查询商品限购信息入参
 * @date 2018/6/8 14:15
 */
public class QueryGoodsLimitInfoRequestVo extends BaseRequest {

    private Long goodsId;

    private Long bizId;

    private Integer bizType;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

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
