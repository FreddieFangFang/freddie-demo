package com.weimob.saas.ec.limitation.model.response;

import java.io.Serializable;

/**
 * @author lujialin
 * @description 商品限购出参
 * @date 2018/7/24 10:16
 */
public class QueryGoodsLimitDetailVo implements Serializable {
    private static final long serialVersionUID = 3443874437927486253L;

    private Long pid;

    private Long goodsId;

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
}
