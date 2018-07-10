package com.weimob.saas.ec.limitation.facade;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitNumListResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationQueryBizService;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    public QueryGoodsLimitNumListResponseVo queryGoodsLimitNumList(QueryGoodsLimitNumRequestVo requestVo) {

        validateQueryGoodsLimitInfoRequestVo(requestVo);
        return limitationQueryBizService.queryGoodsLimitNumList(requestVo);
    }

    public QueryActivityLimitInfoResponseVo queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo) {

        validateQueryActivityLimitInfoRequestVo(requestVo);
        return limitationQueryBizService.queryActivityLimitInfo(requestVo);
    }

    private void validateQueryActivityLimitInfoRequestVo(QueryActivityLimitInfoRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, requestVo.getPid());
        //VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, requestVo.getStoreId());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, requestVo.getBizId());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, requestVo.getBizType());
    }

    private void validateQueryGoodsLimitInfoRequestVo(QueryGoodsLimitNumRequestVo requestVo) {
        VerifyParamUtils.checkListParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo.getQueryGoodslimitNumVoList());
        for (QueryGoodsLimitNumListVo vo : requestVo.getQueryGoodslimitNumVoList()) {
            VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, vo.getPid());
            VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, vo.getStoreId());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, vo.getBizId());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, vo.getBizType());
            VerifyParamUtils.checkParam(LimitationErrorCode.GOODSID_IS_NULL, vo.getGoodsId());
            if (Objects.equals(vo.getBizType(), ActivityTypeEnum.DISCOUNT.getType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.ACTIVITY_STOCK_TYPE_IS_NULL, vo.getActivityStockType());
            }
        }
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
            if (Objects.equals(request.getBizType(), ActivityTypeEnum.PRIVILEGE_PRICE.getType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.SKUID_IS_NULL, request.getSkuId());
            }
            if (Objects.equals(request.getBizType(), ActivityTypeEnum.DISCOUNT.getType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.ACTIVITY_STOCK_TYPE_IS_NULL, request.getActivityStockType());
                if (Objects.equals(request.getActivityStockType(), LimitConstant.ACTIVITY_SKU_TYPE)) {
                    VerifyParamUtils.checkParam(LimitationErrorCode.SKUID_IS_NULL, request.getSkuId());
                }
            }
        }
    }
}
