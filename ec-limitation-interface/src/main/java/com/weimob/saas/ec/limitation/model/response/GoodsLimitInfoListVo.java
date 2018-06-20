package com.weimob.saas.ec.limitation.model.response;

import com.weimob.saas.ec.common.request.BaseRequest;

/**
 * @author lujialin
 * @description 商品限购出参信息
 * @date 2018/6/4 11:21
 */
public class GoodsLimitInfoListVo extends BaseRequest {

    private Long goodsId;

    private Long skuId;
    /**
     * 是否限购
     */
    private Boolean limitStatus;
    /**
     * 还能购买的数量
     */
    private Integer canBuyNum;
    /**
     * 已经购买的数量
     */
    private Integer alreadyBuyNum;

    private Long bizId;

    private Integer bizType;

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
}
