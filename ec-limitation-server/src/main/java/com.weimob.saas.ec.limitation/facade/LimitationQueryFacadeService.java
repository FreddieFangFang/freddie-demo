package com.weimob.saas.ec.limitation.facade;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.*;
import com.weimob.saas.ec.limitation.service.LimitationQueryBizService;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.apache.commons.collections.CollectionUtils;
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

    public QueryGoodsLimitDetailListResponseVo queryGoodsLimitDetailList(QueryGoodsLimitDetailListRequestVo requestVo) {
        validateQueryGoodsLimitDetailListRequestVo(requestVo);

        return limitationQueryBizService.queryGoodsLimitDetailList(requestVo);
    }

    public QueryActivityLimitInfoListResponseVo queryActivityLimitInfoList(QueryActivityLimitInfoListRequestVo requestVo) {
        validateQueryActivityLimitInfoListRequestVo(requestVo);

        return limitationQueryBizService.queryActivityLimitInfoList(requestVo);
    }

    private void validateQueryGoodsLimitDetailListRequestVo(QueryGoodsLimitDetailListRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo);
        VerifyParamUtils.checkListParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo.getGoodsList());
        for (QueryGoodsLimitDetailListVo vo : requestVo.getGoodsList()) {
            VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, vo.getPid());
            VerifyParamUtils.checkParam(LimitationErrorCode.WID_IS_NULL, vo.getWid());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, vo.getBizId());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, vo.getBizType());
            VerifyParamUtils.checkParam(LimitationErrorCode.GOODSID_IS_NULL, vo.getGoodsId());
        }
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
        if (CollectionUtils.isEmpty(requestVo.getGoodsDetailList())) {
            throw new LimitationBizException(LimitationErrorCode.REQUEST_PARAM_IS_NULL);
        }
        for (QueryGoodsLimitInfoListVo request : requestVo.getGoodsDetailList()) {
            VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, request.getPid());
            VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, request.getStoreId());
            VerifyParamUtils.checkParam(LimitationErrorCode.WID_IS_NULL, request.getWid());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, request.getBizId());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, request.getBizType());
            if (!Objects.equals(request.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.GOODSID_IS_NULL, request.getGoodsId());
            }
            VerifyParamUtils.checkParam(LimitationErrorCode.LIMITLEVEL_IS_NULL, request.getLimitLevel());
            VerifyParamUtils.checkParam(LimitationErrorCode.LIMITTYPE_IS_NULL, request.getLimitType());
            VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, request.getCheckLimit());
            if (request.getCheckLimit()) {
                VerifyParamUtils.checkParam(LimitationErrorCode.GOODSNUM_IS_NULL, request.getGoodsBuyNum());
            }
            if (Objects.equals(request.getBizType(), ActivityTypeEnum.PRIVILEGE_PRICE.getType())
                    || Objects.equals(request.getBizType(), ActivityTypeEnum.COMMUNITY_GROUPON.getType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.SKUID_IS_NULL, request.getSkuId());
            }
            if (Objects.equals(request.getBizType(), ActivityTypeEnum.DISCOUNT.getType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.ACTIVITY_STOCK_TYPE_IS_NULL, request.getActivityStockType());
                if (Objects.equals(request.getActivityStockType(), LimitConstant.DISCOUNT_TYPE_SKU)) {
                    VerifyParamUtils.checkParam(LimitationErrorCode.SKUID_IS_NULL, request.getSkuId());
                }
            }
        }
    }

    private void validateQueryActivityLimitInfoListRequestVo(QueryActivityLimitInfoListRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, requestVo.getPid());
        VerifyParamUtils.checkListParam(LimitationErrorCode.BIZID_IS_NULL, requestVo.getBizIds());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, requestVo.getBizType());
    }
}
