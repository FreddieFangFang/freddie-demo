package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.DeleteDiscountUserLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.model.response.SaveGoodsLimitInfoResponseVo;

/**
 * @author lujialin
 * @description 限购更新service层
 * @date 2018/5/29 10:53
 */
public interface LimitationUpdateBizService {

    LimitationUpdateResponseVo saveLimitationInfo(LimitationInfoRequestVo requestVo);

    LimitationUpdateResponseVo updateLimitationInfo(LimitationInfoRequestVo requestVo);

    LimitationUpdateResponseVo deleteLimitationInfo(DeleteLimitationRequestVo requestVo);

    LimitationUpdateResponseVo batchDeleteGoodsLimit(BatchDeleteGoodsLimitRequestVo requestVo);

    SaveGoodsLimitInfoResponseVo saveGoodsLimitInfo(SaveGoodsLimitInfoRequestVo requestVo);

    SaveGoodsLimitInfoResponseVo updateGoodsLimitInfo(SaveGoodsLimitInfoRequestVo requestVo);

    DeleteDiscountUserLimitInfoResponseVo deleteDiscountUserLimitInfo(DeleteDiscountUserLimitInfoRequestVo requestVo);
}
