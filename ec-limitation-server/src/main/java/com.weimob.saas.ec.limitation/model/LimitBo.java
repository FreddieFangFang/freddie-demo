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
    Map<String, Integer> orderGoodsLimitMap = new HashMap();

    private Map<Long, UserLimitBaseBo> activityIdLimitMap = new HashMap<>();

    private Map<Long, UserLimitBaseBo> goodsIdLimitMap = new HashMap<>();

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
