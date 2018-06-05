package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.request.DeductUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.ReverseUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.SaveUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.response.ReverseUserLimitResponseVo;
import com.weimob.saas.ec.limitation.model.response.UpdateUserLimitResponseVo;
import com.weimob.soa.common.response.SoaResponse;

/**
 * @author lujialin
 * @description 用户限购校验service
 * @date 2018/6/5 17:32
 */
public interface UserLimitUpdateService {

    SoaResponse<UpdateUserLimitResponseVo, LimitationErrorCode> saveUserLimit(SaveUserLimitRequestVo requestVo);


    SoaResponse<UpdateUserLimitResponseVo, LimitationErrorCode> deductUserLimit(DeductUserLimitRequestVo requestVo);


    SoaResponse<ReverseUserLimitResponseVo, LimitationErrorCode> reverseUserLimit(ReverseUserLimitRequestVo requestVo);
}
