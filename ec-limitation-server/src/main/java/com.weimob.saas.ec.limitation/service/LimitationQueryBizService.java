package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.*;

/**
 * @author lujialin
 * @description 限购service层
 * @date 2018/5/29 10:52
 */
public interface LimitationQueryBizService {

    GoodsLimitInfoListResponseVo queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo);

    QueryGoodsLimitNumListResponseVo queryGoodsLimitNumList(QueryGoodsLimitNumRequestVo requestVo);

    QueryActivityLimitInfoResponseVo queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo);

    QueryGoodsLimitDetailListResponseVo queryGoodsLimitDetailList(QueryGoodsLimitDetailListRequestVo requestVo);

    QueryActivityLimitInfoListResponseVo queryActivityLimitInfoList(QueryActivityLimitInfoListRequestVo requestVo);

}
