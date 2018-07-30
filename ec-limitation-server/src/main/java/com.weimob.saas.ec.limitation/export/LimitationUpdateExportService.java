package com.weimob.saas.ec.limitation.export;

import com.weimob.saas.ec.common.exception.BaseException;
import com.weimob.saas.ec.common.exception.CommonErrorCode;
import com.weimob.saas.ec.common.export.BaseExportService;
import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.facade.LimitationUpdateFacadeService;
import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.DeleteDiscountUserLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.model.response.SaveGoodsLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationUpdateService;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.soa.common.response.SoaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lujialin
 * @description 限购更改export层
 * @date 2018/5/29 10:41
 */
@Service(value = "limitationUpdateExportService")
public class LimitationUpdateExportService extends BaseExportService implements LimitationUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimitationUpdateExportService.class);

    @Autowired
    private LimitationUpdateFacadeService limitationUpdateFacadeService;

    @Override
    public SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> saveLimitationInfo(LimitationInfoRequestVo requestVo) {

        SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> soaResponse = process(limitationUpdateFacadeService, "saveLimitationInfo", requestVo);
        if (soaResponse.getResponseVo() != null) {
            soaResponse.setLogBizData(String.valueOf(soaResponse.getResponseVo().getLimitId()));
        }
        return soaResponse;
    }

    @Override
    public SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> updateLimitationInfo(LimitationInfoRequestVo requestVo) {
        SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> soaResponse = process(limitationUpdateFacadeService, "updateLimitationInfo", requestVo);
        if (soaResponse.getResponseVo() != null) {
            soaResponse.setLogBizData(String.valueOf(soaResponse.getResponseVo().getLimitId()));
        }
        return soaResponse;
    }

    @Override
    public SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> deleteLimitationInfo(DeleteLimitationRequestVo requestVo) {
        SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> soaResponse = new SoaResponse<>();
        LimitationUpdateResponseVo responseVo = null;

        try {
            LimitContext.setTicket(soaResponse.getMonitorTrackId());
            responseVo = limitationUpdateFacadeService.deleteLimitationInfo(requestVo);
            LimitContext.clearAll();
            soaResponse.setResponseVo(responseVo);
        } catch (BaseException baseException) {
            LOGGER.error(" throw biz exception!, monitorTrackId:" + soaResponse.getMonitorTrackId(), baseException);
            soaResponse.setProcessResult(false);
            soaResponse.setReturnCode(baseException.getErrorCode());
            soaResponse.setReturnMsg(baseException.getErrorMsg());
        } catch (Throwable t) {
            LOGGER.error(" throw system exception!, monitorTrackId:" + soaResponse.getMonitorTrackId(), t);
            soaResponse.setProcessResult(false);
            soaResponse.setReturnCode(CommonErrorCode.FAIL.getErrorCode());
            soaResponse.setReturnMsg(CommonErrorCode.FAIL.getErrorMsg());
        }

        return soaResponse;
    }

    @Override
    public SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> batchDeleteGoodsLimit(BatchDeleteGoodsLimitRequestVo requestVo) {
        SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> soaResponse = new SoaResponse<>();
        LimitationUpdateResponseVo responseVo = null;

        try {
            LimitContext.setTicket(soaResponse.getMonitorTrackId());
            responseVo = limitationUpdateFacadeService.batchDeleteGoodsLimit(requestVo);
            LimitContext.clearAll();
            soaResponse.setResponseVo(responseVo);
        } catch (BaseException baseException) {
            LOGGER.error(" throw biz exception!, monitorTrackId:" + soaResponse.getMonitorTrackId(), baseException);
            soaResponse.setProcessResult(false);
            soaResponse.setReturnCode(baseException.getErrorCode());
            soaResponse.setReturnMsg(baseException.getErrorMsg());
        } catch (Throwable t) {
            LOGGER.error(" throw system exception!, monitorTrackId:" + soaResponse.getMonitorTrackId(), t);
            soaResponse.setProcessResult(false);
            soaResponse.setReturnCode(CommonErrorCode.FAIL.getErrorCode());
            soaResponse.setReturnMsg(CommonErrorCode.FAIL.getErrorMsg());
        }

        return soaResponse;
    }

    @Override
    public SoaResponse<SaveGoodsLimitInfoResponseVo, LimitationCommonErrorVo> saveGoodsLimitInfo(SaveGoodsLimitInfoRequestVo requestVo) {
        SoaResponse<SaveGoodsLimitInfoResponseVo, LimitationCommonErrorVo> soaResponse = new SoaResponse<>();
        SaveGoodsLimitInfoResponseVo responseVo = null;

        try {
            LimitContext.setTicket(soaResponse.getMonitorTrackId());
            responseVo = limitationUpdateFacadeService.saveGoodsLimitInfo(requestVo);
            LimitContext.clearAll();
            soaResponse.setResponseVo(responseVo);
        } catch (BaseException baseException) {
            LOGGER.error(" throw biz exception!, monitorTrackId:" + soaResponse.getMonitorTrackId(), baseException);
            soaResponse.setProcessResult(false);
            soaResponse.setReturnCode(baseException.getErrorCode());
            soaResponse.setReturnMsg(baseException.getErrorMsg());
        } catch (Throwable t) {
            LOGGER.error(" throw system exception!, monitorTrackId:" + soaResponse.getMonitorTrackId(), t);
            soaResponse.setProcessResult(false);
            soaResponse.setReturnCode(CommonErrorCode.FAIL.getErrorCode());
            soaResponse.setReturnMsg(CommonErrorCode.FAIL.getErrorMsg());
        }

        return soaResponse;
    }

    @Override
    public SoaResponse<SaveGoodsLimitInfoResponseVo, LimitationCommonErrorVo> updateGoodsLimitInfo(SaveGoodsLimitInfoRequestVo requestVo) {
        SoaResponse<SaveGoodsLimitInfoResponseVo, LimitationCommonErrorVo> soaResponse = new SoaResponse<>();
        SaveGoodsLimitInfoResponseVo responseVo = null;

        try {
            LimitContext.setTicket(soaResponse.getMonitorTrackId());
            responseVo = limitationUpdateFacadeService.updateGoodsLimitInfo(requestVo);
            LimitContext.clearAll();
            soaResponse.setResponseVo(responseVo);
        } catch (BaseException baseException) {
            LOGGER.error(" throw biz exception!, monitorTrackId:" + soaResponse.getMonitorTrackId(), baseException);
            soaResponse.setProcessResult(false);
            soaResponse.setReturnCode(baseException.getErrorCode());
            soaResponse.setReturnMsg(baseException.getErrorMsg());
        } catch (Throwable t) {
            LOGGER.error(" throw system exception!, monitorTrackId:" + soaResponse.getMonitorTrackId(), t);
            soaResponse.setProcessResult(false);
            soaResponse.setReturnCode(CommonErrorCode.FAIL.getErrorCode());
            soaResponse.setReturnMsg(CommonErrorCode.FAIL.getErrorMsg());
        }

        return soaResponse;
    }

    @Override
    public SoaResponse<DeleteDiscountUserLimitInfoResponseVo, LimitationCommonErrorVo> deleteDiscountUserLimitInfo(DeleteDiscountUserLimitInfoRequestVo requestVo) {
        return process(limitationUpdateFacadeService, "deleteDiscountUserLimitInfo", requestVo);
    }

}
