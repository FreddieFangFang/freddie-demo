package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.soa.common.response.SoaResponse;

/**
 * @author lujialin
 * @description 限购更新service
 * @date 2018/5/29 10:22
 */
public interface LimitationUpdateService {
    /**
     *  
     * @title 保存限购主要信息
     * @author lujialin
     * @date 2018/5/29 10:45
     * @useScene 保存限购主要信息
     * @parameterExample
     * @returnExample
     * @param 
     * @return 
     */
    SoaResponse<LimitationUpdateResponseVo,LimitationCommonErrorVo> saveLimitationInfo(LimitationInfoRequestVo requestVo);
}
