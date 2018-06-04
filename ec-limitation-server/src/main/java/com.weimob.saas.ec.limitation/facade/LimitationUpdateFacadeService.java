package com.weimob.saas.ec.limitation.facade;

import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.request.BatchDeleteGoodsLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.BatchDeleteGoodsLimitVo;
import com.weimob.saas.ec.limitation.model.request.DeleteLimitationRequestVo;
import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationUpdateBizService;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lujialin
 * @description 限购更改facade层
 * @date 2018/5/29 10:48
 */
@Service(value = "limitationUpdateFacadeService")
public class LimitationUpdateFacadeService {

    @Autowired
    private LimitationUpdateBizService limitationUpdateBizService;

    /**
     * 保存限购主要信息
     *
     * @param requestVo
     * @return
     */
    public LimitationUpdateResponseVo saveLimitationInfo(LimitationInfoRequestVo requestVo) {
        //校验参数
        validateRequestParam(requestVo);

        return limitationUpdateBizService.saveLimitationInfo(requestVo);
    }

    private void validateRequestParam(LimitationInfoRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo);
        VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, requestVo.getPid());
        VerifyParamUtils.checkListParam(LimitationErrorCode.STORE_IS_NULL, requestVo.getStoreIdList());
        VerifyParamUtils.checkParam(LimitationErrorCode.CHANNELTYPE_IS_NULL, requestVo.getChannelType());
        VerifyParamUtils.checkParam(LimitationErrorCode.SOURCE_IS_NULL, requestVo.getSource());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, requestVo.getBizId());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, requestVo.getBizType());
        VerifyParamUtils.checkParam(LimitationErrorCode.LIMITLEVEL_IS_NULL, requestVo.getLimitLevel());
        VerifyParamUtils.checkParam(LimitationErrorCode.LIMITNUM_IS_NULL, requestVo.getLimitNum());
        VerifyParamUtils.checkParam(LimitationErrorCode.LIMITTYPE_IS_NULL, requestVo.getLimitType());
    }

    /**
     * 更新限购主要信息
     *
     * @param requestVo
     * @return
     */
    public LimitationUpdateResponseVo updateLimitationInfo(LimitationInfoRequestVo requestVo) {
        //校验参数
        validateRequestParam(requestVo);

        return limitationUpdateBizService.updateLimitationInfo(requestVo);
    }


    public LimitationUpdateResponseVo deleteLimitationInfo(DeleteLimitationRequestVo requestVo) {

        validateDeleteRequestParam(requestVo);

        return limitationUpdateBizService.deleteLimitationInfo(requestVo);
    }

    private void validateDeleteRequestParam(DeleteLimitationRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo);
        VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, requestVo.getPid());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, requestVo.getBizId());
        VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, requestVo.getBizType());
    }

    public LimitationUpdateResponseVo batchDeleteGoodsLimit(BatchDeleteGoodsLimitRequestVo requestVo) {

        validateBatchDeleteRequestParam(requestVo);

        return limitationUpdateBizService.batchDeleteGoodsLimit(requestVo);
    }

    private void validateBatchDeleteRequestParam(BatchDeleteGoodsLimitRequestVo requestVo) {
        VerifyParamUtils.checkParam(LimitationErrorCode.REQUEST_PARAM_IS_NULL, requestVo);
        for (BatchDeleteGoodsLimitVo request : requestVo.getDeleteGoodsLimitVoList()) {
            VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, request.getPid());
            VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, request.getStoreId());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, request.getBizId());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, request.getBizType());
            VerifyParamUtils.checkParam(LimitationErrorCode.GOODSID_IS_NULL, request.getBizType());
        }
    }

}
