package com.weimob.saas.ec.limitation.model.response;

import com.weimob.saas.ec.limitation.model.request.SkuLimitInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 商品限购出参
 * @date 2018/7/24 10:16
 */
public class QueryGoodsLimitDetailVo implements Serializable {
    private static final long serialVersionUID = 3443874437927486253L;

    private Long pid;

    private Long storeId;

    private Long goodsId;

    private Long bizId;

    private Integer bizType;

    /**
     * 实际可售数量
     */
    private Integer realSoldNum;

    /**
     * 商品还能再购买数量-wid
     */
    private Integer goodsCanBuyNum;

    /**
     * 商品是否限购
     */
    private Boolean goodsLimit;

    /**
     * 用户已买的商品数量-wid
     */
    private Integer alreadyBuyGoodsNum;

    private List<SkuLimitInfo> skuLimitInfoList;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getRealSoldNum() {
        return realSoldNum;
    }

    public void setRealSoldNum(Integer realSoldNum) {
        this.realSoldNum = realSoldNum;
    }

    public Integer getGoodsCanBuyNum() {
        return goodsCanBuyNum;
    }

    public void setGoodsCanBuyNum(Integer goodsCanBuyNum) {
        this.goodsCanBuyNum = goodsCanBuyNum;
    }

    public Boolean getGoodsLimit() {
        return goodsLimit;
    }

    public void setGoodsLimit(Boolean goodsLimit) {
        this.goodsLimit = goodsLimit;
    }

    public List<SkuLimitInfo> getSkuLimitInfoList() {
        return skuLimitInfoList;
    }

    public void setSkuLimitInfoList(List<SkuLimitInfo> skuLimitInfoList) {
        this.skuLimitInfoList = skuLimitInfoList;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
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

    public Integer getAlreadyBuyGoodsNum() {
        return alreadyBuyGoodsNum;
    }

    public void setAlreadyBuyGoodsNum(Integer alreadyBuyGoodsNum) {
        this.alreadyBuyGoodsNum = alreadyBuyGoodsNum;
    }
}
