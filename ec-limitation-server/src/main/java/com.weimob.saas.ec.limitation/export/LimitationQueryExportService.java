package com.weimob.saas.ec.limitation.export;

import com.weimob.saas.ec.common.export.BaseExportService;
import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.facade.LimitationQueryFacadeService;
import com.weimob.saas.ec.limitation.model.request.GoodsLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationQueryService;
import com.weimob.soa.common.response.SoaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lujialin
 * @description 限购查询export层
 * @date 2018/5/29 10:41
 */
@Service(value = "limitationQueryExportService")
public class LimitationQueryExportService extends BaseExportService implements LimitationQueryService {

    @Autowired
    private LimitationQueryFacadeService limitationQueryFacadeService;

    @Override
    public SoaResponse<GoodsLimitInfoListResponseVo, LimitationCommonErrorVo> queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo) {
        return process(limitationQueryFacadeService, "queryGoodsLimitInfoList", requestVo);
    }

    @Override
    public SoaResponse<QueryGoodsLimitInfoResponseVo, LimitationErrorCode> queryGoodsLimitInfo(QueryGoodsLimitInfoRequestVo requestVo) {
        return process(limitationQueryFacadeService, "queryGoodsLimitInfo", requestVo);
    }
}
