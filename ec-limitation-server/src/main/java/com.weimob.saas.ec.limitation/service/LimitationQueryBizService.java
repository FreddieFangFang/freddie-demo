package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.model.request.GoodsLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitInfoResponseVo;

/**
 * @author lujialin
 * @description 限购service层
 * @date 2018/5/29 10:52
 */
public interface LimitationQueryBizService {

    GoodsLimitInfoListResponseVo queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo);

    QueryGoodsLimitInfoResponseVo queryGoodsLimitInfo(QueryGoodsLimitInfoRequestVo requestVo);

    QueryActivityLimitInfoResponseVo queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo);
}
