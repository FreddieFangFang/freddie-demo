package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;
import com.weimob.saas.ec.limitation.common.LimitLevelEnum;
import com.weimob.saas.ec.limitation.common.LimitTypeEnum;

/**
 * @author lujialin
 * @description 商品限购入参详细信息
 * @date 2018/6/4 11:00
 */
public class QueryGoodsLimitInfoListVo extends BaseRequest {

    /**
     * 活动限购的活动id，商品限购的goodsId
     */
    private Long bizId;

    /**
     * 活动类型的活动类型，商品限购类型暂定30
     */
    private Integer bizType;

    /**
     * 限时折扣1.冻结库存；2.可用sku
     */
    private Integer activityStockType;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 商品购买数
     */
    private Integer goodsBuyNum;
    /**
     * 结算传true，其他传false
     */
    private Boolean checkLimit;

    /**
     * 限购级别
     */
    private Integer limitLevel;
    /**
     * 限购维度
     */
    private Integer limitType;

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

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getGoodsBuyNum() {
        return goodsBuyNum;
    }

    public void setGoodsBuyNum(Integer goodsBuyNum) {
        this.goodsBuyNum = goodsBuyNum;
    }

    public Boolean getCheckLimit() {
        return checkLimit;
    }

    public void setCheckLimit(Boolean checkLimit) {
        this.checkLimit = checkLimit;
    }

    public Integer getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(Integer limitLevel) {
        this.limitLevel = limitLevel;
    }

    public Integer getLimitType() {
        return limitType;
    }

    public void setLimitType(Integer limitType) {
        this.limitType = limitType;
    }

    public Integer getActivityStockType() {
        return activityStockType;
    }

    public void setActivityStockType(Integer activityStockType) {
        this.activityStockType = activityStockType;
    }
}
