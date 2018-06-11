package com.weimob.saas.ec.limitation.model.request;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 查询商品限购信息入参
 * @date 2018/6/8 14:15
 */
public class QueryGoodsLimitNumRequestVo implements Serializable {

    private List<QueryGoodslimitNumListVo> queryGoodslimitNumVoList;

    public List<QueryGoodslimitNumListVo> getQueryGoodslimitNumVoList() {
        return queryGoodslimitNumVoList;
    }

    public void setQueryGoodslimitNumVoList(List<QueryGoodslimitNumListVo> queryGoodslimitNumVoList) {
        this.queryGoodslimitNumVoList = queryGoodslimitNumVoList;
    }
}
