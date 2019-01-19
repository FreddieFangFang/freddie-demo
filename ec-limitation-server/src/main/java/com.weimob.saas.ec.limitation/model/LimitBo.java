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
    /** 购买件数（限购级别不同，含义则不同） */
    Map<String, Integer> globalOrderBuyNumMap = new HashMap();

    /** 构建更新活动限购表所需信息 */
    private Map<Long, UserLimitBaseBo> activityIdLimitMap = new HashMap<>();

    /** 构建更新商品限购表所需信息 */
    private Map<Long, UserLimitBaseBo> goodsIdLimitMap = new HashMap<>();

    /** 构建更新sku限购表所需信息 */
    private Map<Long, UserLimitBaseBo> skuIdLimitMap = new HashMap<>();

    /** 操作活动表数据信息 */
    List<UserLimitEntity> activityLimitEntityList = new ArrayList<>();

    /** 操作商品表数据信息 */
    List<UserGoodsLimitEntity> goodsLimitEntityList = new ArrayList<>();

    /** 操作sku表数据信息 */
    List<SkuLimitInfoEntity> activityGoodsSoldEntityList = new ArrayList<>();

    /** N元N件规则信息Map */
    private Map<String, Integer> globalRuleNumMap;

    /** 活动级别参与次数 */
    private Map<Long, Integer> globalParticipateTimeMap;

    /** 历史维权商品总数量 */
    private Map<Long, Integer> globalRightsGoodsTotalNumMap;

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

    public Map<Long, Integer> getGlobalRightsGoodsTotalNumMap() {
        return globalRightsGoodsTotalNumMap;
    }

    public void setGlobalRightsGoodsTotalNumMap(Map<Long, Integer> globalRightsGoodsTotalNumMap) {
        this.globalRightsGoodsTotalNumMap = globalRightsGoodsTotalNumMap;
    }
}
