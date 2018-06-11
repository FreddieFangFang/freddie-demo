package com.weimob.saas.ec.limitation.model.response;

import com.weimob.saas.ec.limitation.model.request.SkuLimitInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 查询商品限购信息出参
 * @date 2018/6/8 14:02
 */
public class QueryGoodsLimitNumListResponseVo implements Serializable {
    private static final long serialVersionUID = -6945843800219764729L;

    private List<QueryGoodsLimitNumVo> queryGoodsLimitNumList;

    public List<QueryGoodsLimitNumVo> getQueryGoodsLimitNumList() {
        return queryGoodsLimitNumList;
    }

    public void setQueryGoodsLimitNumList(List<QueryGoodsLimitNumVo> queryGoodsLimitNumList) {
        this.queryGoodsLimitNumList = queryGoodsLimitNumList;
    }
}
