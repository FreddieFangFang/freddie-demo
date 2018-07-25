package com.weimob.saas.ec.limitation.model.request;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 商详、活动列表查询限购信息
 * @date 2018/7/24 10:07
 */
public class QueryGoodsLimitDetailListRequestVo implements Serializable {

    private static final long serialVersionUID = -5802699582333997466L;

    private List<QueryGoodsLimitDetailListVo> goodsList;

    public List<QueryGoodsLimitDetailListVo> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<QueryGoodsLimitDetailListVo> goodsList) {
        this.goodsList = goodsList;
    }
}
