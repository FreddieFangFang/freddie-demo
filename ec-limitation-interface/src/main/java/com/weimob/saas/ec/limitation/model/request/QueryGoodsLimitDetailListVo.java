package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

/**
 * @author lujialin
 * @description 商详页、活动列表页查询限购信息
 * @date 2018/7/24 10:10
 */
public class QueryGoodsLimitDetailListVo extends BaseRequest {

    private static final long serialVersionUID = -3665009399269174710L;

    /**
     * 活动限购的活动id，商品限购的goodsId
     */
    private Long bizId;

    /**
     * 活动类型的活动类型，商品限购类型暂定30
     */
    private Integer bizType;

    /**
     * 商品id
     */
    private Long goodsId;

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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
