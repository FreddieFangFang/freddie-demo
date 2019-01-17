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
    Map<String, Integer> globalOrderBuyNumMap = new HashMap();

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

    /** N元N件规则信息Map */
    private Map<String, Integer> globalRuleNumMap;

    /** 活动级别参与次数 */
    private Map<Long, Integer> globalParticipateTimeMap;

    public Map<String, Integer> getGlobalOrderBuyNumMap() {
        return globalOrderBuyNumMap;
    }

    public void setGlobalOrderBuyNumMap(Map<String, Integer> globalOrderBuyNumMap) {
        this.globalOrderBuyNumMap = globalOrderBuyNumMap;
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

    public Map<String, Integer> getGlobalRuleNumMap() {
        return globalRuleNumMap;
    }

    public Map<Long, Integer> getGlobalParticipateTimeMap() {
        return globalParticipateTimeMap;
    }

    public void setGlobalParticipateTimeMap(Map<Long, Integer> globalParticipateTimeMap) {
        this.globalParticipateTimeMap = globalParticipateTimeMap;
    }

    public void setGlobalRuleNumMap(Map<String, Integer> globalRuleNumMap) {
        this.globalRuleNumMap = globalRuleNumMap;
    }
}
