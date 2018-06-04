package com.weimob.saas.ec.limitation.model;

import com.weimob.saas.ec.common.request.BaseRequest;

import java.util.List;

/**
 * @author lujialin
 * @description 删除商品dao层入参
 * @date 2018/5/31 14:43
 */
public class DeleteGoodsParam extends BaseRequest{
    private Long limitId;
    private List<Long> goodsIdList;

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }

    public List<Long> getGoodsIdList() {
        return goodsIdList;
    }

    public void setGoodsIdList(List<Long> goodsIdList) {
        this.goodsIdList = goodsIdList;
    }
}
