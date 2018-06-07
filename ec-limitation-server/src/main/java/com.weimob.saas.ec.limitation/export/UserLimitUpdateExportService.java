package com.weimob.saas.ec.limitation.export;

import com.weimob.saas.ec.common.exception.BaseException;
import com.weimob.saas.ec.common.exception.CommonErrorCode;
import com.weimob.saas.ec.common.export.BaseExportService;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.facade.UserLimitUpdateFacadeService;
import com.weimob.saas.ec.limitation.model.request.DeductUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.ReverseUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.SaveUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.response.ReverseUserLimitResponseVo;
import com.weimob.saas.ec.limitation.model.response.UpdateUserLimitResponseVo;
import com.weimob.saas.ec.limitation.service.UserLimitUpdateService;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.soa.common.response.SoaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lujialin
 * @description 用户限购export层
 * @date 2018/6/5 18:19
 */
@Service(value = "userLimitUpdateExportService")
public class UserLimitUpdateExportService extends BaseExportService implements UserLimitUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLimitUpdateExportService.class);

    @Autowired
    private UserLimitUpdateFacadeService userLimitUpdateFacadeService;


    @Override
    public SoaResponse<UpdateUserLimitResponseVo, LimitationErrorCode> saveUserLimit(SaveUserLimitRequestVo requestVo) {
        SoaResponse<UpdateUserLimitResponseVo, LimitationErrorCode> soaResponse = new SoaResponse<>();
        try {
            LimitContext.setTicket(soaResponse.getMonitorTrackId());
            UpdateUserLimitResponseVo updateUserLimitResponseVo = userLimitUpdateFacadeService.saveUserLimit(requestVo);

            soaResponse.setResponseVo(updateUserLimitResponseVo);

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
    public SoaResponse<UpdateUserLimitResponseVo, LimitationErrorCode> deductUserLimit(DeductUserLimitRequestVo requestVo) {
        return null;
    }

    @Override
    public SoaResponse<ReverseUserLimitResponseVo, LimitationErrorCode> reverseUserLimit(ReverseUserLimitRequestVo requestVo) {
        return null;
    }
}
