package com.weimob.saas.ec.limitation.model.response;

import com.weimob.saas.ec.common.request.BaseRequest;

/**
 * @author lujialin
 * @description 商品限购出参信息
 * @date 2018/6/4 11:21
 */
public class GoodsLimitInfoListVo extends BaseRequest {

    /** 业务id */
    private Long bizId;

    /** 业务类型 */
    private Integer bizType;

    /** 商品id */
    private Long goodsId;

    /** skuId */
    private Long skuId;

    /** 是否限购 */
    private Boolean limitStatus;

    /** 还能购买的数量，sku级别，购物车接口需要 */
    private Integer canBuyNum;

    /** 已购买数量/已售数量（根据限购级别来区分，活动/商品级别表示用户已购买数量（优惠套装表示已售数量）、SKU级别表示已售数量）*/
    private Integer alreadyBuyNum;

    /** 可售数量阈值 */
    private Integer skuLimitNum;

    /** 商品限购 */
    private Integer goodsLimitNum;

    /** 商品级别还能购买的数量，0代表不可买，不限购返回Integer.MAX_VALUE */
    private Integer goodsCanBuyNum;

    /** 活动限购 */
    private Integer activityLimitNum;

    /** 当前用户已购买数量 */
    private Integer userAlreadyBuyNum;

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

    public Boolean getLimitStatus() {
        return limitStatus;
    }

    public void setLimitStatus(Boolean limitStatus) {
        this.limitStatus = limitStatus;
    }

    public Integer getCanBuyNum() {
        return canBuyNum;
    }

    public void setCanBuyNum(Integer canBuyNum) {
        this.canBuyNum = canBuyNum;
    }

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

    public Integer getAlreadyBuyNum() {
        return alreadyBuyNum;
    }

    public void setAlreadyBuyNum(Integer alreadyBuyNum) {
        this.alreadyBuyNum = alreadyBuyNum;
    }

    public Integer getSkuLimitNum() {
        return skuLimitNum;
    }

    public void setSkuLimitNum(Integer skuLimitNum) {
        this.skuLimitNum = skuLimitNum;
    }

    public Integer getGoodsLimitNum() {
        return goodsLimitNum;
    }

    public void setGoodsLimitNum(Integer goodsLimitNum) {
        this.goodsLimitNum = goodsLimitNum;
    }

    public Integer getGoodsCanBuyNum() {
        return goodsCanBuyNum;
    }

    public void setGoodsCanBuyNum(Integer goodsCanBuyNum) {
        this.goodsCanBuyNum = goodsCanBuyNum;
    }

    public Integer getActivityLimitNum() {
        return activityLimitNum;
    }

    public void setActivityLimitNum(Integer activityLimitNum) {
        this.activityLimitNum = activityLimitNum;
    }

    public Integer getUserAlreadyBuyNum() {
        return userAlreadyBuyNum;
    }

    public void setUserAlreadyBuyNum(Integer userAlreadyBuyNum) {
        this.userAlreadyBuyNum = userAlreadyBuyNum;
    }
}
