package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.request.GoodsLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitDetailListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitNumRequestVo;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitDetailListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitNumListResponseVo;

import java.util.List;

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

    public List<SkuLimitInfoEntity> getSkuLimitInfoList(List<SkuLimitInfoEntity> queryList);

    }
