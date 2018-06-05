package com.weimob.saas.ec.limitation.model.request;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 查询商品限购数入参
 * @date 2018/6/4 10:57
 */
public class GoodsLimitInfoListRequestVo implements Serializable {
    private static final long serialVersionUID = 8063898089190732781L;

    private List<QueryGoodsLimitInfoListVo> goodsDetailList;

    public List<QueryGoodsLimitInfoListVo> getGoodsDetailList() {
        return goodsDetailList;
    }

    public void setGoodsDetailList(List<QueryGoodsLimitInfoListVo> goodsDetailList) {
        this.goodsDetailList = goodsDetailList;
    }
}
