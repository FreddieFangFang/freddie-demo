package com.weimob.saas.ec.limitation.handler.biz;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.handler.LimitBizChain;
import com.weimob.saas.ec.limitation.handler.limit.GoodsLimitBizHandler;
import com.weimob.saas.ec.limitation.model.LimitBo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lujialin
 * @description 增加限购handler
 * @date 2018/6/6 14:18
 */
@Service(value = "saveUserLimitHandler")
public class SaveUserLimitHandler extends BaseHandler<UpdateUserLimitVo> {

    @Autowired
    private GoodsLimitBizHandler goodsLimitBizHandler;
    @Autowired
    private LimitationServiceImpl limitationService;

    @Override
    protected void checkParams(List<UpdateUserLimitVo> vos) {
        super.checkParams(vos);
        for (UpdateUserLimitVo limitVo : vos) {
            VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, limitVo.getPid());
            VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, limitVo.getStoreId());
            VerifyParamUtils.checkParam(LimitationErrorCode.GOODSID_IS_NULL, limitVo.getGoodsId());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, limitVo.getBizType());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, limitVo.getBizId());
            VerifyParamUtils.checkParam(LimitationErrorCode.GOODSNUM_IS_NULL, limitVo.getGoodsNum());
            if (limitVo.getGoodsNum() < 1) {
                throw new LimitationBizException(LimitationErrorCode.GOODSNUM_IS_ILLEGAL);
            }
            VerifyParamUtils.checkParam(LimitationErrorCode.ORDERNO_IS_NULL, limitVo.getOrderNo());
            VerifyParamUtils.checkParam(LimitationErrorCode.WID_IS_NULL, limitVo.getWid());
        }
    }


    @Override
    protected void doBatchBizLogic(List<UpdateUserLimitVo> vos) {
        //处理限购逻辑，分成三个handler，分别处理活动级别、商品级别、sku级别的限购校验
        //limitBizChain.execute();
        if (Objects.equals(vos.get(0).getBizType(), 30)) {
            goodsLimitBizHandler.doLimitHandler(vos);
            limitationService.saveUserLimitRecode(LimitContext.getLimitBo().getGoodsLimitEntityList(),
                    LimitContext.getLimitBo().getActivityLimitEntityList(), LimitContext.getLimitBo().getActivityGoodsSoldEntityList(),
                    LimitContext.getLimitBo().getActivityLimitRecodeList(),
                    LimitContext.getLimitBo().getUserGoodsLimitRecodeList(), LimitContext.getLimitBo().getUpdate());
        }
    }

}
