package com.weimob.saas.ec.limitation.model;

import com.weimob.saas.ec.common.request.BaseRequest;

import java.util.List;

/**
 * @author lujialin
 * @description 删除sku入参
 * @date 2018/7/18 18:12
 */
public class DeleteSkuParam extends BaseRequest {
    private Long limitId;
    private Long goodsId;
    private List<Long> skuIdList;

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public List<Long> getSkuIdList() {
        return skuIdList;
    }

    public void setSkuIdList(List<Long> skuIdList) {
        this.skuIdList = skuIdList;
    }
}
