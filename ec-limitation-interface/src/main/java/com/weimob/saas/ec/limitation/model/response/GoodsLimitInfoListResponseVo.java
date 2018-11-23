package com.weimob.saas.ec.limitation.model.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 查询商品限购出参
 * @date 2018/6/4 10:58
 */
public class GoodsLimitInfoListResponseVo implements Serializable {
    private static final long serialVersionUID = -8573201396970923905L;

    private List<GoodsLimitInfoListVo> goodsLimitInfoList;

    public GoodsLimitInfoListResponseVo() {
    }

    public GoodsLimitInfoListResponseVo(List<GoodsLimitInfoListVo> goodsLimitInfoList) {
        this.goodsLimitInfoList = goodsLimitInfoList;
    }

    public List<GoodsLimitInfoListVo> getGoodsLimitInfoList() {
        return goodsLimitInfoList;
    }

    public void setGoodsLimitInfoList(List<GoodsLimitInfoListVo> goodsLimitInfoList) {
        this.goodsLimitInfoList = goodsLimitInfoList;
    }
}
