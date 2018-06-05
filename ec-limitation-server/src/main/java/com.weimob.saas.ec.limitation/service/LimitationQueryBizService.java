package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.model.request.GoodsLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListResponseVo;

/**
 * @author lujialin
 * @description 限购service层
 * @date 2018/5/29 10:52
 */
public interface LimitationQueryBizService {

    GoodsLimitInfoListResponseVo queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo);
}
