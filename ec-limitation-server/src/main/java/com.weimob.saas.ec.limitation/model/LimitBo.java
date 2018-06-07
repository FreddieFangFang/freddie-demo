package com.weimob.saas.ec.limitation.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lujialin
 * @description 限购信息
 * @date 2018/6/6 14:52
 */
public class LimitBo {
    Map<String, Integer> orderGoodsLimitMap = new HashMap();

    private Map<Long, UserLimitBaseBo> activityIdLimitMap = new HashMap<>();

    private Map<Long, UserLimitBaseBo> goodsIdLimitMap = new HashMap<>();

    private Map<Long, UserLimitBaseBo> skuIdLimitMap = new HashMap<>();

    public Map<String, Integer> getOrderGoodsLimitMap() {
        return orderGoodsLimitMap;
    }

    public void setOrderGoodsLimitMap(Map<String, Integer> orderGoodsLimitMap) {
        this.orderGoodsLimitMap = orderGoodsLimitMap;
    }

    public Map<Long, UserLimitBaseBo> getActivityIdLimitMap() {
        return activityIdLimitMap;
    }

    public void setActivityIdLimitMap(Map<Long, UserLimitBaseBo> activityIdLimitMap) {
        this.activityIdLimitMap = activityIdLimitMap;
    }

    public Map<Long, UserLimitBaseBo> getGoodsIdLimitMap() {
        return goodsIdLimitMap;
    }

    public void setGoodsIdLimitMap(Map<Long, UserLimitBaseBo> goodsIdLimitMap) {
        this.goodsIdLimitMap = goodsIdLimitMap;
    }

    public Map<Long, UserLimitBaseBo> getSkuIdLimitMap() {
        return skuIdLimitMap;
    }

    public void setSkuIdLimitMap(Map<Long, UserLimitBaseBo> skuIdLimitMap) {
        this.skuIdLimitMap = skuIdLimitMap;
    }
}
