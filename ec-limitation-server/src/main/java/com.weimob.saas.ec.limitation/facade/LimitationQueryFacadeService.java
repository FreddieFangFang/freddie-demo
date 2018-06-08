package com.weimob.saas.ec.limitation.facade;

import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.request.GoodsLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitInfoListVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationQueryBizService;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lujialin
 * @description 限购查询facade层
 * @date 2018/5/29 10:49
 */
@Service(value = "limitationQueryFacadeService")
public class LimitationQueryFacadeService {

    @Autowired
    private LimitationQueryBizService limitationQueryBizService;

    public GoodsLimitInfoListResponseVo queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo) {

        validateRequestParam(requestVo);

        return limitationQueryBizService.queryGoodsLimitInfoList(requestVo);
    }

    public QueryGoodsLimitInfoResponseVo queryGoodsLimitInfo(QueryGoodsLimitInfoRequestVo requestVo) {

        validateQueryGoodsLimitInfoRequestVo(requestVo);
        return limitationQueryBizService.queryGoodsLimitInfo(requestVo);
    }

    public QueryActivityLimitInfoResponseVo queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo) {

        validateQueryActivityLimitInfoRequestVo(requestVo);
        return limitationQueryBizService.queryActivityLimitInfo(requestVo);
    }

    private void validateQueryActivityLimitInfoRequestVo(QueryActivityLimitInfoRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, requestVo.getPid());
        VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, requestVo.getStoreId());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, requestVo.getBizId());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, requestVo.getBizType());
    }

    private void validateQueryGoodsLimitInfoRequestVo(QueryGoodsLimitInfoRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, requestVo.getPid());
        VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, requestVo.getStoreId());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, requestVo.getBizId());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, requestVo.getBizType());
        VerifyParamUtils.checkParam(LimitationErrorCode.GOODSID_IS_NULL, requestVo.getGoodsId());
    }

    private void validateRequestParam(GoodsLimitInfoListRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo);
        for (QueryGoodsLimitInfoListVo request : requestVo.getGoodsDetailList()) {
            VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, request.getPid());
            VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, request.getStoreId());
            VerifyParamUtils.checkParam(LimitationErrorCode.WID_IS_NULL, request.getWid());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, request.getBizId());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, request.getBizType());
            VerifyParamUtils.checkParam(LimitationErrorCode.GOODSID_IS_NULL, request.getGoodsId());
            VerifyParamUtils.checkParam(LimitationErrorCode.LIMITLEVEL_IS_NULL, request.getLimitLevel());
            VerifyParamUtils.checkParam(LimitationErrorCode.LIMITTYPE_IS_NULL, request.getLimitType());
            VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, request.getCheckLimit());
            if (request.getCheckLimit()) {
                VerifyParamUtils.checkParam(LimitationErrorCode.GOODSNUM_IS_NULL, request.getGoodsBuyNum());
            }
        }
    }
}
