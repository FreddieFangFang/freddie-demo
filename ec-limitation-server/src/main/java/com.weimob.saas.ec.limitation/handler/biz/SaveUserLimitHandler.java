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
        checkCreateOrDeductOrderParams(vos);
    }

    @Override
    protected void doBatchBizLogic(List<UpdateUserLimitVo> updateUserLimitVoList) {
        //处理限购逻辑，分成三个handler，分别处理活动级别、商品级别、sku级别的限购校验
        //按活动类型进行分组
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
            } else if (Objects.equals(vos.get(0).getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())||
                       Objects.equals(vos.get(0).getBizType(), ActivityTypeEnum.COMMUNITY_GROUPON.getType())) {
                activityLimitBizHandler.doLimitHandler(vos);
                skuLimitBizHandler.doLimitHandler(vos);
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
        Map<String, UserLimitEntity> activityMap = new HashMap<>();
        Map<String, UserGoodsLimitEntity> goodsLimitMap = new HashMap<>();
        Map<String, SkuLimitInfoEntity> skuLimitMap = new HashMap<>();

        // 2.构建更新数据库入参
        reverseSaveOrDeductUserLimit(logList, activityMap, goodsLimitMap, skuLimitMap);

        // 3.回滚活动商品的下单记录
        try {
            limitationService.updateUserLimitRecord(new ArrayList<>(goodsLimitMap.values()), new ArrayList<>(activityMap.values()), new ArrayList<>(skuLimitMap.values()));
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_ERROR, e);
        }
    }
}
