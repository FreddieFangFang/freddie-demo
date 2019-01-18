package com.weimob.saas.ec.limitation.handler.biz;

import com.alibaba.fastjson.JSON;
import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitServiceNameEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.UserGoodsLimitDao;
import com.weimob.saas.ec.limitation.entity.*;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.model.BizContentBo;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.utils.CommonBizUtil;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    protected void doBatchBizLogic(List<UpdateUserLimitVo> updateUserLimitVoList) {
        //1.幂等校验
        if (!Objects.equals(LimitServiceNameEnum.RIGHTS_DEDUCT_LIMIT.name(), updateUserLimitVoList.get(0).getLimitServiceName())) {
            validRepeatDeductLimitNum(updateUserLimitVoList);
        }

        //2.按活动类型分组
        Map<Integer, List<UpdateUserLimitVo>> activityMap = buildActivityMap(updateUserLimitVoList);
        Iterator<Map.Entry<Integer, List<UpdateUserLimitVo>>> iterator = activityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<UpdateUserLimitVo>> entry = iterator.next();
            List<UpdateUserLimitVo> vos = entry.getValue();
            Integer bizType = vos.get(0).getBizType();
            Integer activityStockType = vos.get(0).getActivityStockType();
            Map<String, Integer> localOrderBuyNumMap = new HashMap<>();
            Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap = new HashMap<>();

            //3.更新限购记录
            //3.1 判断活动类型
            if (CommonBizUtil.isValidActivityLimit(bizType)) {
                groupingOrderActivityRequestVoList(LimitContext.getLimitBo().getGlobalOrderBuyNumMap(), orderGoodsQueryMap, vos, localOrderBuyNumMap);
            }
            if (CommonBizUtil.isValidGoodsLimit(bizType)) {
                groupingOrderGoodsRequestVoList(LimitContext.getLimitBo().getGlobalOrderBuyNumMap(), orderGoodsQueryMap, vos, localOrderBuyNumMap);
            }
            if (CommonBizUtil.isValidSkuLimit(bizType, activityStockType)) {
                groupingOrderSkuRequestVoList(LimitContext.getLimitBo().getGlobalOrderBuyNumMap(), orderGoodsQueryMap, vos, localOrderBuyNumMap);
            }
            super.updateUserLimitRecord(localOrderBuyNumMap);
        }
        // 3.2 操作数据库
        limitationService.updateUserLimitRecord(LimitContext.getLimitBo().getGoodsLimitEntityList(), LimitContext.getLimitBo().getActivityLimitEntityList(), LimitContext.getLimitBo().getActivityGoodsSoldEntityList());
    }

    @Override
    protected void checkParams(List<UpdateUserLimitVo> vos) {
        super.checkParams(vos);
        checkCreateOrDeductOrderParams(vos);
    }

    private void validRepeatDeductLimitNum(List<UpdateUserLimitVo> vos) {
        //1. 根据订单查询日志是否有记录
        LimitOrderChangeLogEntity queryLogParameter = new LimitOrderChangeLogEntity();
        queryLogParameter.setReferId(vos.get(0).getOrderNo().toString());
        queryLogParameter.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
        queryLogParameter.setServiceName(getServiceName().name());
        queryLogParameter.setBizType(vos.get(0).getBizType());
        List<LimitOrderChangeLogEntity> limitOrderChangeLogEntity = null;
        try {
            limitOrderChangeLogEntity = limitOrderChangeLogDao.getLogByReferId(queryLogParameter);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_ORDER_CHANGE_LOG_ERROR, e);
        }

        //2. 如果有记录直接抛出异常
        if (CollectionUtils.isNotEmpty(limitOrderChangeLogEntity)) {
            throw new LimitationBizException(LimitationErrorCode.REPEAT_ORDER_DEDUCT_LIMIT);
        }

    }

    @Override
    protected LimitOrderChangeLogEntity createOrderChangeLog(UpdateUserLimitVo vo) {
        LimitInfoEntity limitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(vo.getPid(), vo.getBizId(), vo.getBizType(), LimitConstant.DELETED));
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
        // 取消订单 N元N件需要记录content（次数）
        if (Objects.equals(ActivityTypeEnum.NYNJ.getType(), vo.getBizType())) {
            BizContentBo bizContent = new BizContentBo(LimitContext.getLimitBo().getGlobalParticipateTimeMap().get(vo.getBizId()));
            orderChangeLogEntity.setContent(JSON.toJSONString(bizContent));
        } else if (vo.getRightId() != null) {
            orderChangeLogEntity.setContent(vo.getRightId().toString());
        }
        orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
        return orderChangeLogEntity;
    }

    @Override
    protected LimitServiceNameEnum getServiceName() {
        return LimitServiceNameEnum.DEDUCT_USER_LIMIT;
    }

    @Override
    public void doReverse(List<LimitOrderChangeLogEntity> logList) {

        // 1.分组商品，统计wid下活动，商品的回滚数量
        Map<String, UserLimitEntity> activityMap = new HashMap<>();
        Map<String, UserGoodsLimitEntity> goodsLimitMap = new HashMap<>();
        Map<String, SkuLimitInfoEntity> skuLimitMap = new HashMap<>();
        Integer bizType = null;

        // 2.构建更新数据库入参
        reverseSaveOrDeductUserLimit(logList, activityMap, goodsLimitMap, skuLimitMap);

        // 3.回滚活动商品的下单记录
        try {
            limitationService.saveUserLimitRecord(new ArrayList<>(goodsLimitMap.values()), new ArrayList<>(activityMap.values()), new ArrayList<>(skuLimitMap.values()));
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.CANCEL_ORDER_REVERSE_FAIL, e);
        }
    }
}
