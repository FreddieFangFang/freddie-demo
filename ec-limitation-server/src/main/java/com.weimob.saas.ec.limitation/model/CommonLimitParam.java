package com.weimob.saas.ec.limitation.model;

import com.weimob.saas.ec.common.request.BaseRequest;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;

import java.util.List;
import java.util.Set;

/**
 * @description //查询商品限购入参
 * @author haojie.jin
 * @date 6:04 PM 2018/11/7
 * @param
 * @return
 **/

public class CommonLimitParam extends BaseRequest {
    private Long limitId;
    private Set<Long> goodsIdList;
    private List<Long> skuIdList;
    private List<Long> limitIdList;
    private List<SkuLimitInfoEntity> skuLimitInfoEntityList;

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }


    public Set<Long> getGoodsIdList() {
        return goodsIdList;
    }

    public void setGoodsIdList(Set<Long> goodsIdList) {
        this.goodsIdList = goodsIdList;
    }

    public List<Long> getSkuIdList() {
        return skuIdList;
    }

    public void setSkuIdList(List<Long> skuIdList) {
        this.skuIdList = skuIdList;
    }

    public List<Long> getLimitIdList() {
        return limitIdList;
    }

    public void setLimitIdList(List<Long> limitIdList) {
        this.limitIdList = limitIdList;
    }

    public List<SkuLimitInfoEntity> getSkuLimitInfoEntityList() {
        return skuLimitInfoEntityList;
    }

    public void setSkuLimitInfoEntityList(List<SkuLimitInfoEntity> skuLimitInfoEntityList) {
        this.skuLimitInfoEntityList = skuLimitInfoEntityList;
    }
}
