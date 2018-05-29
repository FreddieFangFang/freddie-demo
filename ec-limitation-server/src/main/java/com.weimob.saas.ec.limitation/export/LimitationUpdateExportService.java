package com.weimob.saas.ec.limitation.export;

import com.weimob.saas.ec.common.export.BaseExportService;
import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.facade.LimitationUpdateFacadeService;
import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationUpdateService;
import com.weimob.soa.common.response.SoaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lujialin
 * @description 限购更改export层
 * @date 2018/5/29 10:41
 */
@Service(value = "limitationUpdateExportService")
public class LimitationUpdateExportService extends BaseExportService implements LimitationUpdateService {

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

}
