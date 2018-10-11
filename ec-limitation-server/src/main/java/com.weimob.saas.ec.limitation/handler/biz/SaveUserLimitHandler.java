package com.weimob.saas.ec.limitation.handler.biz;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitServiceNameEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
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

import java.util.*;

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
            if (Objects.equals(limitVo.getBizType(), ActivityTypeEnum.DISCOUNT.getType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.ACTIVITY_STOCK_TYPE_IS_NULL, limitVo.getActivityStockType());
            }
            if (Objects.equals(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), limitVo.getBizType())
                    || (Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), limitVo.getBizType())
                    && Objects.equals(limitVo.getActivityStockType(), LimitConstant.DISCOUNT_TYPE_SKU))
                    || Objects.equals(LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel(), limitVo.getBizType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.SKUINFO_IS_NULL, limitVo.getSkuId());
            }
        }
    }

    @Override
    protected void doBatchBizLogic(List<UpdateUserLimitVo> updateUserLimitVoList) {
        //处理限购逻辑，分成三个handler，分别处理活动级别、商品级别、sku级别的限购校验
        //limitBizChain.execute();
        //限购商品的类型分组
        Map<Integer, List<UpdateUserLimitVo>> activityMap = buildActivityMap(updateUserLimitVoList);
        Iterator<Map.Entry<Integer, List<UpdateUserLimitVo>>> iterator = activityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<UpdateUserLimitVo>> entry = iterator.next();
            List<UpdateUserLimitVo> vos = entry.getValue();
            if (Objects.equals(vos.get(0).getBizType(), LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel())) {
                goodsLimitBizHandler.doLimitHandler(vos);
                skuLimitBizHandler.doLimitHandler(vos);
            } else if (Objects.equals(vos.get(0).getBizType(), ActivityTypeEnum.PRIVILEGE_PRICE.getType())
                    || (Objects.equals(vos.get(0).getBizType(), ActivityTypeEnum.DISCOUNT.getType())
                    && Objects.equals(vos.get(0).getActivityStockType(), LimitConstant.DISCOUNT_TYPE_SKU))) {
                goodsLimitBizHandler.doLimitHandler(vos);
                activityLimitBizHandler.doLimitHandler(vos);
                skuLimitBizHandler.doLimitHandler(vos);
            } else if (Objects.equals(vos.get(0).getBizType(), ActivityTypeEnum.DISCOUNT.getType())) {
                goodsLimitBizHandler.doLimitHandler(vos);
                activityLimitBizHandler.doLimitHandler(vos);
            }
        }
        limitationService.saveUserLimitRecord(LimitContext.getLimitBo().getGoodsLimitEntityList(),
                LimitContext.getLimitBo().getActivityLimitEntityList(), LimitContext.getLimitBo().getActivityGoodsSoldEntityList());

    }

    @Override
    protected LimitOrderChangeLogEntity createOrderChangeLog(UpdateUserLimitVo vo) {
        LimitInfoEntity limitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(vo.getPid(), vo.getBizId(), vo.getBizType()));
        LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
        orderChangeLogEntity.setPid(vo.getPid());
        orderChangeLogEntity.setStoreId(vo.getStoreId());
        orderChangeLogEntity.setBizId(vo.getBizId());
        orderChangeLogEntity.setBizType(vo.getBizType());
        orderChangeLogEntity.setBuyNum(vo.getGoodsNum());
        orderChangeLogEntity.setGoodsId(vo.getGoodsId());
        if (Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), vo.getBizType())
                && Objects.equals(vo.getActivityStockType(), LimitConstant.DISCOUNT_TYPE_STOCK)) {

        } else {
            orderChangeLogEntity.setSkuId(vo.getSkuId());
        }
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
        Map<Long, UserLimitEntity> activityMap = new HashMap<>();
        Map<Long, UserGoodsLimitEntity> goodsLimitMap = new HashMap<>();
        Map<Long, SkuLimitInfoEntity> skuLimitMap = new HashMap<>();
        Integer bizType = null;

        for (LimitOrderChangeLogEntity logEntity : logList) {
            bizType = logEntity.getBizType();
            if (goodsLimitMap.get(logEntity.getGoodsId()) == null) {
                UserGoodsLimitEntity userGoodsLimitEntity = new UserGoodsLimitEntity();
                userGoodsLimitEntity.setPid(logEntity.getPid());
                userGoodsLimitEntity.setStoreId(logEntity.getStoreId());
                userGoodsLimitEntity.setLimitId(logEntity.getLimitId());
                userGoodsLimitEntity.setGoodsId(logEntity.getGoodsId());
                userGoodsLimitEntity.setWid(logEntity.getWid());
                userGoodsLimitEntity.setBuyNum(logEntity.getBuyNum());
                goodsLimitMap.put(logEntity.getGoodsId(), userGoodsLimitEntity);
            } else {
                goodsLimitMap.get(logEntity.getGoodsId()).setBuyNum(goodsLimitMap.get(logEntity.getGoodsId()).getBuyNum() + logEntity.getBuyNum());
            }

            if (activityMap.get(logEntity.getBizId()) == null) {
                UserLimitEntity userLimitEntity = new UserLimitEntity();
                userLimitEntity.setPid(logEntity.getPid());
                userLimitEntity.setStoreId(logEntity.getStoreId());
                userLimitEntity.setLimitId(logEntity.getLimitId());
                userLimitEntity.setWid(logEntity.getWid());
                userLimitEntity.setBuyNum(logEntity.getBuyNum());
                activityMap.put(logEntity.getBizId(), userLimitEntity);
            } else {
                activityMap.get(logEntity.getBizId()).setBuyNum(activityMap.get(logEntity.getBizId()).getBuyNum() + logEntity.getBuyNum());
            }

            if (logEntity.getSkuId() == null) {
                continue;
            }
            if (skuLimitMap.get(logEntity.getSkuId()) == null) {
                SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
                skuLimitInfoEntity.setPid(logEntity.getPid());
                skuLimitInfoEntity.setStoreId(logEntity.getStoreId());
                skuLimitInfoEntity.setLimitId(logEntity.getLimitId());
                skuLimitInfoEntity.setGoodsId(logEntity.getGoodsId());
                skuLimitInfoEntity.setSoldNum(logEntity.getBuyNum());
                skuLimitInfoEntity.setSkuId(logEntity.getSkuId());
                skuLimitMap.put(logEntity.getSkuId(), skuLimitInfoEntity);
            } else {
                skuLimitMap.get(logEntity.getSkuId()).setSoldNum(skuLimitMap.get(logEntity.getSkuId()).getSoldNum() + logEntity.getBuyNum());
            }
        }

        //2. 回滚活动商品的下单记录
        try {
            if (Objects.equals(bizType, ActivityTypeEnum.PRIVILEGE_PRICE.getType())) {
                limitationService.updateUserLimitRecord(new ArrayList<UserGoodsLimitEntity>(goodsLimitMap.values()), new ArrayList<UserLimitEntity>(activityMap.values()), new ArrayList<SkuLimitInfoEntity>(skuLimitMap.values()));
            } else if (Objects.equals(bizType, ActivityTypeEnum.DISCOUNT.getType())) {
                limitationService.updateUserLimitRecord(new ArrayList<UserGoodsLimitEntity>(goodsLimitMap.values()), new ArrayList<UserLimitEntity>(activityMap.values()), new ArrayList<SkuLimitInfoEntity>(skuLimitMap.values()));
            } else if (Objects.equals(bizType, LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel())) {
                limitationService.updateUserLimitRecord(new ArrayList<UserGoodsLimitEntity>(goodsLimitMap.values()), null, new ArrayList<SkuLimitInfoEntity>(skuLimitMap.values()));
            }
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_ERROR, e);
        }
    }
}
