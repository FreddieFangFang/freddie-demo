package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;
import com.weimob.saas.ec.limitation.common.LimitLevelEnum;
import com.weimob.saas.ec.limitation.common.LimitTypeEnum;

import java.util.List;

/**
 * @author lujialin
 * @description 保存限购商品入参
 * @date 2018/6/4 17:47
 */
public class SaveGoodsLimitInfoRequestVo extends BaseRequest{
    /**
     * 活动限购的活动id，商品限购的goodsId
     */
    private Long bizId;

    /**
     * 活动类型的活动类型，商品限购类型暂定30
     */
    private Integer bizType;

    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 限购级别
     */
    private Integer limitLevel;
    /**
     * 限购维度
     */
    private Integer goodsLimitType;
    /**
     * 商品限购数
     */
    private Integer goodsLimitNum;
    /**
     * sku限购信息
     */
    private List<SkuLimitInfo> skuLimitInfoList;
    /**
     * 限购渠道类型
     */
    private String channelType;
    /**
     * 限购来源
     */
    private String source;

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
}
