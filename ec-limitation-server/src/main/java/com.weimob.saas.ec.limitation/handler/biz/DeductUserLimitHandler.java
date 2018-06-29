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
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.SkuLimitInfo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lujialin
 * @description 减少限购handler
 * @scenes 下单成功
 * @date 2018/6/6 14:22
 */
@Service(value = "deductUserLimitHandler")
public class DeductUserLimitHandler extends BaseHandler<UpdateUserLimitVo> {

    private static Logger LOGGER = Logger.getLogger(DeductUserLimitHandler.class);

    @Autowired
    private LimitationServiceImpl limitationService;
    @Autowired
    private UserGoodsLimitDao userGoodsLimitDao;

    @Override
    protected void doBatchBizLogic(List<UpdateUserLimitVo> vos) {
        //1.幂等校验
        validRepeatDeductLimitNum(vos);

        //2.分组
        Map<String, Integer> orderGoodsLimitMap = new HashMap<>();
        Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap = new HashMap<>();

        super.groupingOrderGoodsRequestVoList(LimitContext.getLimitBo().getOrderGoodsLimitMap(), orderGoodsQueryMap, vos, orderGoodsLimitMap);

        //3.更新限购记录
        //3.1 判断活动类型
        if (Objects.equals(vos.get(0).getBizType(), LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel())) {
            super.updateUserLimitRecord(orderGoodsLimitMap);
            // 3.2 操作数据库
            limitationService.updateUserLimitRecord(LimitContext.getLimitBo().getGoodsLimitEntityList(), null, null);
        } else if (Objects.equals(vos.get(0).getBizType(), ActivityTypeEnum.PRIVILEGE_PRICE.getType())) {

            groupingOrderActivityRequestVoList(LimitContext.getLimitBo().getOrderGoodsLimitMap(), orderGoodsQueryMap, vos, orderGoodsLimitMap);
            groupingOrderSkuRequestVoList(LimitContext.getLimitBo().getOrderGoodsLimitMap(), orderGoodsQueryMap, vos, orderGoodsLimitMap);
            super.updateUserLimitRecord(LimitContext.getLimitBo().getOrderGoodsLimitMap());

            limitationService.updateUserLimitRecord(LimitContext.getLimitBo().getGoodsLimitEntityList(), LimitContext.getLimitBo().getActivityLimitEntityList(), LimitContext.getLimitBo().getActivityGoodsSoldEntityList());
        } else if (Objects.equals(vos.get(0).getBizType(), ActivityTypeEnum.DISCOUNT.getType())) {

            groupingOrderActivityRequestVoList(LimitContext.getLimitBo().getOrderGoodsLimitMap(), orderGoodsQueryMap, vos, orderGoodsLimitMap);
            super.updateUserLimitRecord(LimitContext.getLimitBo().getOrderGoodsLimitMap());

            limitationService.updateUserLimitRecord(LimitContext.getLimitBo().getGoodsLimitEntityList(), LimitContext.getLimitBo().getActivityLimitEntityList(), null);
        }
    }

    @Override
    protected void checkParams(List<UpdateUserLimitVo> vos) {
        super.checkParams(vos);
        // TODO 重复代码，抽象出来
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

    private void validRepeatDeductLimitNum(List<UpdateUserLimitVo> vos) {
        //1. 根据订单查询日志是否有记录
        LimitOrderChangeLogEntity queryLogParameter = new LimitOrderChangeLogEntity();
        queryLogParameter.setReferId(vos.get(0).getOrderNo().toString());
        queryLogParameter.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
        queryLogParameter.setServiceName(getServiceName().name());
        LimitOrderChangeLogEntity limitOrderChangeLogEntity = null;
        try {
            // SQL 默认查询 service_name = "DEDUCT_USER_LIMIT"
            limitOrderChangeLogEntity = limitOrderChangeLogDao.selectByPrimaryKey(Long.valueOf(queryLogParameter.getReferId()));
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_ORDER_CHANGE_LOG_ERROR, e);
        }

        //2. 如果有记录直接抛出异常
        if (null != limitOrderChangeLogEntity) {
            throw new LimitationBizException(LimitationErrorCode.REPEAT_ORDER_DEDUCT_LIMIT);
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
        orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_OVER);
        return orderChangeLogEntity;
    }

    @Override
    protected LimitServiceNameEnum getServiceName() {
        return LimitServiceNameEnum.DEDUCT_USER_LIMIT;
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
                limitationService.saveUserLimitRecord(new ArrayList<UserGoodsLimitEntity>(goodsLimitMap.values()), new ArrayList<UserLimitEntity>(activityMap.values()), new ArrayList<SkuLimitInfoEntity>(skuLimitMap.values()));
            } else if (Objects.equals(bizType, ActivityTypeEnum.DISCOUNT.getType())) {
                limitationService.saveUserLimitRecord(new ArrayList<UserGoodsLimitEntity>(goodsLimitMap.values()), new ArrayList<UserLimitEntity>(activityMap.values()), null);
            } else if (Objects.equals(bizType, LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel())) {
                limitationService.saveUserLimitRecord(new ArrayList<UserGoodsLimitEntity>(goodsLimitMap.values()), null, null);
            }
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_ERROR, e);
        }
    }
}
