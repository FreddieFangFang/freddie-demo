package com.weimob.saas.ec.limitation.handler;

import com.weimob.saas.ec.limitation.model.LimitBo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;
import java.util.Map;

/**
 * @author lujialin
 * @description 限购校验基础handler
 * @date 2018/6/6 15:20
 */
public interface LimitBizHandler {

    void doLimitHandler(List<UpdateUserLimitVo> vos);


}
