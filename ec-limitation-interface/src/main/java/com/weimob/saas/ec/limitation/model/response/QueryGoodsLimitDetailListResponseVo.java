package com.weimob.saas.ec.limitation.model.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 商详活动专题页查询限购信息出参
 * @date 2018/7/24 10:14
 */
public class QueryGoodsLimitDetailListResponseVo implements Serializable {
    private static final long serialVersionUID = 8907402263131096630L;

    private List<QueryGoodsLimitDetailVo> queryGoodsLimitDetailVoList;

    public List<QueryGoodsLimitDetailVo> getQueryGoodsLimitDetailVoList() {
        return queryGoodsLimitDetailVoList;
    }

    public void setQueryGoodsLimitDetailVoList(List<QueryGoodsLimitDetailVo> queryGoodsLimitDetailVoList) {
        this.queryGoodsLimitDetailVoList = queryGoodsLimitDetailVoList;
    }
}
