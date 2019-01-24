package com.weimob.saas.ec.limitation.handler.biz;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
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
import com.weimob.saas.ec.limitation.model.BizContentBo;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.utils.CommonBizUtil;
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
            Integer bizType = vos.get(0).getBizType();
            Integer activityStockType = vos.get(0).getActivityStockType();
            //处理活动限购
            if (CommonBizUtil.isValidActivityLimit(bizType)) {
                activityLimitBizHandler.doLimitHandler(vos);
            }
            //处理商品限购
            if (CommonBizUtil.isValidGoodsLimit(bizType)) {
                goodsLimitBizHandler.doLimitHandler(vos);
            }
            //处理SKU限购
            if (CommonBizUtil.isValidSkuLimit(bizType, activityStockType)) {
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
        orderChangeLogEntity.setSkuId(vo.getSkuId());
        orderChangeLogEntity.setLimitId(limitInfoEntity.getLimitId());
        orderChangeLogEntity.setWid(vo.getWid());
        if (RpcContext.getContext().getGlobalTicket().startsWith("EC_STRESS-")) {
            orderChangeLogEntity.setTicket(RpcContext.getContext().getGlobalTicket());
        } else {
            orderChangeLogEntity.setTicket(LimitContext.getTicket());
        }
        orderChangeLogEntity.setServiceName(getServiceName().name());
        orderChangeLogEntity.setReferId(vo.getOrderNo().toString());
        orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
        if (Objects.equals(ActivityTypeEnum.NYNJ.getType(), vo.getBizType())) {
            // 下单 记录content（活动规则 + 本次活动参与次数）
            BizContentBo bizContent = new BizContentBo(vo.getRuleNum(), LimitContext.getLimitBo().getGlobalParticipateTimeMap().get(vo.getBizId()));
            orderChangeLogEntity.setContent(JSON.toJSONString(bizContent));
        }
        if (Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), vo.getBizType())) {
            BizContentBo bizContent = new BizContentBo();
            bizContent.setActivityStockType(vo.getActivityStockType());
            orderChangeLogEntity.setContent(JSON.toJSONString(bizContent));
        }
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
            throw new LimitationBizException(LimitationErrorCode.CREATE_ORDER_REVERSE_FAIL, e);
        }
    }
}
