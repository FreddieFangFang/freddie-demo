package com.weimob.saas.ec.limitation.handler.limit;

import com.weimob.saas.ec.limitation.handler.LimitBizHandler;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lujialin
 * @description sku限购校验handler
 * @date 2018/6/6 15:53
 */
@Service(value = "skuLimitBizHandler")
public class SkuLimitBizHandler implements LimitBizHandler {
    @Override
    public void doLimitHandler(List<UpdateUserLimitVo> vos) {

    }
}
