package com.weimob.saas.ec.limitation.model;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lujialin
 * @description 限购信息
 * @date 2018/6/6 14:52
 */
public class LimitBo {
    /**
     * 活动对应的购买的商品件数
     * 商品对应的购买件数
     * sku对应的购买件数
     */
    Map<String, Integer> orderGoodsLimitMap = new HashMap();

    /**
     * 活动id对应的要插入到活动限购表需要的信息
     */
    private Map<Long, UserLimitBaseBo> activityIdLimitMap = new HashMap<>();

    /**
     * 商品id对应的要插入到商品限购表需要的信息
     */
    private Map<Long, UserLimitBaseBo> goodsIdLimitMap = new HashMap<>();

    /**
     * skuid对应的要插入到sku限购表需要的信息
     */
    private Map<Long, UserLimitBaseBo> skuIdLimitMap = new HashMap<>();


    List<UserGoodsLimitEntity> goodsLimitEntityList = new ArrayList<>();
    List<UserLimitEntity> activityLimitEntityList = new ArrayList<>();
    List<SkuLimitInfoEntity> activityGoodsSoldEntityList = new ArrayList<>();


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

    public List<UserGoodsLimitEntity> getGoodsLimitEntityList() {
        return goodsLimitEntityList;
    }

    public void setGoodsLimitEntityList(List<UserGoodsLimitEntity> goodsLimitEntityList) {
        this.goodsLimitEntityList = goodsLimitEntityList;
    }

    public List<UserLimitEntity> getActivityLimitEntityList() {
        return activityLimitEntityList;
    }

    public void setActivityLimitEntityList(List<UserLimitEntity> activityLimitEntityList) {
        this.activityLimitEntityList = activityLimitEntityList;
    }

    public List<SkuLimitInfoEntity> getActivityGoodsSoldEntityList() {
        return activityGoodsSoldEntityList;
    }

    public void setActivityGoodsSoldEntityList(List<SkuLimitInfoEntity> activityGoodsSoldEntityList) {
        this.activityGoodsSoldEntityList = activityGoodsSoldEntityList;
    }
}
