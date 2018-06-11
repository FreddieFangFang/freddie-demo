package com.weimob.saas.ec.limitation.handler.biz;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitServiceNameEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.UserGoodsLimitDao;
import com.weimob.saas.ec.limitation.entity.*;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.handler.limit.ActivityLimitBizHandler;
import com.weimob.saas.ec.limitation.handler.limit.GoodsLimitBizHandler;
import com.weimob.saas.ec.limitation.handler.limit.SkuLimitBizHandler;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private ActivityLimitBizHandler activityLimitBizHandler;
    @Autowired
    private SkuLimitBizHandler skuLimitBizHandler;
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
            if (Objects.equals(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), limitVo.getBizType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.SKUINFO_IS_NULL, limitVo.getSkuId());
            }
        }
    }

    @Override
    protected void doBatchBizLogic(List<UpdateUserLimitVo> vos) {
        //处理限购逻辑，分成三个handler，分别处理活动级别、商品级别、sku级别的限购校验
        //limitBizChain.execute();
        //TODO 限购商品的类型分组
        if (Objects.equals(vos.get(0).getBizType(), LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel())) {
            goodsLimitBizHandler.doLimitHandler(vos);
            limitationService.saveUserLimitRecord(LimitContext.getLimitBo().getGoodsLimitEntityList(),
                    LimitContext.getLimitBo().getActivityLimitEntityList(), LimitContext.getLimitBo().getActivityGoodsSoldEntityList());
        } else if (Objects.equals(vos.get(0).getBizType(), ActivityTypeEnum.PRIVILEGE_PRICE.getType())) {
            goodsLimitBizHandler.doLimitHandler(vos);
            activityLimitBizHandler.doLimitHandler(vos);
            skuLimitBizHandler.doLimitHandler(vos);
            limitationService.saveUserLimitRecord(LimitContext.getLimitBo().getGoodsLimitEntityList(),
                    LimitContext.getLimitBo().getActivityLimitEntityList(), LimitContext.getLimitBo().getActivityGoodsSoldEntityList());
        }

    }

    @Override
    protected LimitOrderChangeLogEntity createOrderChangeLog(UpdateUserLimitVo vo) {
        LimitInfoEntity limitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(vo.getPid(), vo.getBizId(), vo.getBizType()));
        LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
        orderChangeLogEntity.setPid(vo.getPid());
        orderChangeLogEntity.setStoreId(vo.getStoreId());
        orderChangeLogEntity.setBizId(vo.getBizId());
        orderChangeLogEntity.setBizType(vo.getBizType());
        orderChangeLogEntity.setBuyNum(vo.getGoodsNum());
        orderChangeLogEntity.setGoodsId(vo.getGoodsId());
        orderChangeLogEntity.setSkuId(vo.getSkuId());
        orderChangeLogEntity.setLimitId(limitInfoEntity.getLimitId());
        orderChangeLogEntity.setWid(vo.getWid());
        orderChangeLogEntity.setTicket(LimitContext.getTicket());
        orderChangeLogEntity.setServiceName(getServiceName().name());
        orderChangeLogEntity.setReferId(vo.getOrderNo().toString());
        orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
        return orderChangeLogEntity;
    }

    @Override
    protected LimitServiceNameEnum getServiceName() {
        return LimitServiceNameEnum.SAVE_USER_LIMIT;
    }

    @Override
    public void doReverse(List<LimitOrderChangeLogEntity> logList) {

        //1. 分组商品，统计wid下活动，商品的回滚数量
        List<UserGoodsLimitEntity> userGoodsLimitEntityList = new ArrayList<>(logList.size());
        UserGoodsLimitEntity userGoodsLimitEntity = null;

        for (LimitOrderChangeLogEntity logEntity : logList) {
            userGoodsLimitEntity = new UserGoodsLimitEntity();
            userGoodsLimitEntity.setPid(logEntity.getPid());
            userGoodsLimitEntity.setStoreId(logEntity.getStoreId());
            userGoodsLimitEntity.setLimitId(logEntity.getLimitId());
            userGoodsLimitEntity.setGoodsId(logEntity.getGoodsId());
            userGoodsLimitEntity.setWid(logEntity.getWid());
            userGoodsLimitEntity.setBuyNum(logEntity.getBuyNum());
            userGoodsLimitEntityList.add(userGoodsLimitEntity);
        }

        //2. 回滚活动商品的下单记录
        try {
            limitationService.updateUserLimitRecord(userGoodsLimitEntityList, null, null);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_ERROR, e);
        }
    }
}
