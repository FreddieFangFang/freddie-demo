package com.weimob.saas.ec.limitation.model.response;

import com.weimob.saas.ec.limitation.model.request.SkuLimitInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 查询商品限购出参
 * @date 2018/6/11 15:46
 */
public class QueryGoodsLimitNumVo implements Serializable {
    private static final long serialVersionUID = -7855186589695354286L;
    /**
     * 商户id
     */
    private Long pid;
    /**
     * 门店id
     */
    private Long storeId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品限购数
     */
    private Integer goodsLimitNum;
    /**
     * sku的限购信息
     */
    private List<SkuLimitInfo> skuLimitInfoList;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
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
}
