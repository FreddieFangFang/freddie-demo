package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

import java.util.List;

/**
 * @author lujialin
 * @description 添加活动商品改成批量接口
 * @date 2018/7/6 10:18
 */
public class SaveGoodsLimitInfoVo extends BaseRequest {
    /**
     * 业务id（活动id/积分商城商品id）
     */
    private Long bizId;

    /**
     * 业务类型，积分商城暂定30
     */
    private Integer bizType;

    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 限购级别，取值范围，参见：{@link com.weimob.saas.ec.limitation.common.LimitLevelEnum}
     */
    private Integer limitLevel;
    /**
     * 限购对每个人还是所有人，取值范围，参见：{@link com.weimob.saas.ec.limitation.common.LimitTypeEnum}
     */
    private Integer goodsLimitType;
    /**
     * 商品限购数
     */
    private Integer goodsLimitNum;
    /**
     * 店铺级商品限购
     */
    private Integer pidGoodsLimitNum;
    /**
     * sku限购信息
     */
    private List<SkuLimitInfo> skuLimitInfoList;
    /**
     * 限购渠道类型（0：线上；1：线下；0,1：线上+线下；）
     */
    private String channelType;
    /**
     * 限购来源（0：公众号，1：小程序；目前传0,1）
     */
    private String source;
    /**
     * 限时折扣须传，其他不传（1.冻结库存；2.可用sku）
     */
    private Integer activityStockType;

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

    public Integer getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(Integer limitLevel) {
        this.limitLevel = limitLevel;
    }

    public Integer getGoodsLimitType() {
        return goodsLimitType;
    }

    public void setGoodsLimitType(Integer goodsLimitType) {
        this.goodsLimitType = goodsLimitType;
    }

    public Integer getGoodsLimitNum() {
        return goodsLimitNum;
    }

    public void setGoodsLimitNum(Integer goodsLimitNum) {
        this.goodsLimitNum = goodsLimitNum;
    }

    public List<SkuLimitInfo> getSkuLimitInfoList() {
        return skuLimitInfoList;
    }

    public void setSkuLimitInfoList(List<SkuLimitInfo> skuLimitInfoList) {
        this.skuLimitInfoList = skuLimitInfoList;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getPidGoodsLimitNum() {
        return pidGoodsLimitNum;
    }

    public void setPidGoodsLimitNum(Integer pidGoodsLimitNum) {
        this.pidGoodsLimitNum = pidGoodsLimitNum;
    }

    public Integer getActivityStockType() {
        return activityStockType;
    }

    public void setActivityStockType(Integer activityStockType) {
        this.activityStockType = activityStockType;
    }
}
