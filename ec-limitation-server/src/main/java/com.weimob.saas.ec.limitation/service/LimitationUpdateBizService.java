package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;

/**
 * @author lujialin
 * @description 限购更新service层
 * @date 2018/5/29 10:53
 */
public interface LimitationUpdateBizService {

    LimitationUpdateResponseVo saveLimitationInfo(LimitationInfoRequestVo requestVo);
}
