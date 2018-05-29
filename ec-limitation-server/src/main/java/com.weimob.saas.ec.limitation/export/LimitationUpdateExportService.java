package com.weimob.saas.ec.limitation.export;

import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationUpdateService;
import com.weimob.soa.common.response.SoaResponse;

/**
 * @author lujialin
 * @description 限购更改export层
 * @date 2018/5/29 10:41
 */
public class LimitationUpdateExportService implements LimitationUpdateService {

    @Override
    public SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> saveLimitationInfo(LimitationInfoRequestVo requestVo) {
        return null;
    }
}
