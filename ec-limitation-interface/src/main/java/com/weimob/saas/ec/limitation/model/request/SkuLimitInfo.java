package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.limitation.common.LimitTypeEnum;

import java.io.Serializable;

/**
 * @author lujialin
 * @description sku的限购信息
 * @date 2018/6/4 18:00
 */
public class SkuLimitInfo implements Serializable{

    private static final long serialVersionUID = 628342779876026582L;
    private Long skuId;
    /**
     * sku限购数
     */
    private Integer skuLimitNum;

    /**
     * 限购维度
     */
    private Integer skuLimitType;

    /**
     * 已经购买的sku数量
     */
    private Integer alreadySoldNum;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getSkuLimitNum() {
        return skuLimitNum;
    }

    public void setSkuLimitNum(Integer skuLimitNum) {
        this.skuLimitNum = skuLimitNum;
    }

    public Integer getSkuLimitType() {
        return skuLimitType;
    }

    public void setSkuLimitType(Integer skuLimitType) {
        this.skuLimitType = skuLimitType;
    }

    public Integer getAlreadySoldNum() {
        return alreadySoldNum;
    }

    public void setAlreadySoldNum(Integer alreadySoldNum) {
        this.alreadySoldNum = alreadySoldNum;
    }
}
