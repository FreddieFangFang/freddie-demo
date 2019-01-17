package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitServiceNameEnum;

/**
 * @author lujialin
 * @description 减少、增加限购入参vo
 * @date 2018/6/5 17:38
 */
public class UpdateUserLimitVo extends BaseRequest implements Comparable<UpdateUserLimitVo> {

    /**
     * 活动限购的活动id、商品限购的goodsid
     */
    private Long bizId;

    /**
     * 活动类型、商品限购类型，积分商城使用枚举LimitBizTypeEnum.BIZ_TYPE_POINT
     *
     * @see LimitBizTypeEnum
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
     * skuid，商品限购不需要传skuid，活动限购到sku级别的需要传
     */
    private Long skuId;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 订单号
     */
    private Long orderNo;

    /**
     * 维权单号
     */
    private Long rightId;

    /**
     * 调用方服务名 维权用LimitServiceNameEnum.RIGHTS_DEDUCT_LIMIT
     */
    private String limitServiceName;

    /**
     * 规则件数（只有N元N件传）
     */
    private Integer ruleNum;

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

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getActivityStockType() {
        return activityStockType;
    }

    public void setActivityStockType(Integer activityStockType) {
        this.activityStockType = activityStockType;
    }

    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    public String getLimitServiceName() {
        return limitServiceName;
    }

    public void setLimitServiceName(String limitServiceName) {
        this.limitServiceName = limitServiceName;
    }

    public Integer getRuleNum() {
        return ruleNum;
    }

    public void setRuleNum(Integer ruleNum) {
        this.ruleNum = ruleNum;
    }

    @Override
    public int compareTo(UpdateUserLimitVo o) {
        return 0;
    }
}
