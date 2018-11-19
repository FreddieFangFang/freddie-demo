package com.weimob.saas.ec.limitation.service.impl;

import com.alibaba.dubbo.rpc.RpcContext;
import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitLevelEnum;
import com.weimob.saas.ec.limitation.common.LimitServiceNameEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.*;
import com.weimob.saas.ec.limitation.entity.*;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.DeleteDiscountUserLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.model.response.SaveGoodsLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.service.LimitationUpdateBizService;
import com.weimob.saas.ec.limitation.thread.SaveLimitChangeLogThread;
import com.weimob.saas.ec.limitation.utils.IdUtils;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lujialin
 * @description 限购更新service层实现类
 * @date 2018/5/29 11:16
 */
@Service(value = "limitationUpdateBizService")
public class LimitationUpdateBizServiceImpl implements LimitationUpdateBizService {

    @Autowired
    private LimitationServiceImpl limitationService;
    @Autowired
    private ThreadPoolTaskExecutor threadExecutor;
    @Autowired
    private LimitInfoDao limitInfoDao;
    @Autowired
    private GoodsLimitInfoDao goodsLimitInfoDao;
    @Autowired
    private SkuLimitInfoDao skuLimitInfoDao;
    @Autowired
    private LimitStoreRelationshipDao limitStoreRelationshipDao;
    @Autowired
    protected LimitOrderChangeLogDao limitOrderChangeLogDao;


    @Override
    public LimitationUpdateResponseVo saveLimitationInfo(LimitationInfoRequestVo requestVo) {
        // 1.生成全局id
        requestVo.setLimitId(IdUtils.getLimitId(requestVo.getPid()));

        // 2.构建并保存限购主表信息
        buildAndSaveLimitationInfo(requestVo);

        // 3.构建并保存限购门店表信息
        buildAndSaveStoreRelationship(requestVo);

        // 4.构建并保存SKU表信息
        buildAndSaveSkuInfo(requestVo);

        return new LimitationUpdateResponseVo(requestVo.getLimitId(), true);
    }

    @Override
    public LimitationUpdateResponseVo updateLimitationInfo(LimitationInfoRequestVo requestVo) {
        // 1.查询限购主表信息
        LimitInfoEntity oldLimitInfoEntity = getLimitationInfo(requestVo);

        // 2.构建并更新限购主表信息
        LimitInfoEntity limitInfoEntity = buildAndUpdateLimitationInfo(requestVo, oldLimitInfoEntity);

        // 3.构建并更新限购门店表信息
        buildAndUpdateStoreRelationship(requestVo, limitInfoEntity);

        // 4.构建并更新SKU限购信息
        buildAndUpdateSkuInfo(requestVo, oldLimitInfoEntity);

        return new LimitationUpdateResponseVo(oldLimitInfoEntity.getLimitId(), true);
    }

    @Override
    public LimitationUpdateResponseVo deleteLimitationInfo(DeleteLimitationRequestVo requestVo) {
        // 1.查询限购主表信息
        LimitInfoEntity oldLimitInfoEntity = null;
        try {
            oldLimitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_LIMIT_INFO_ERROR, e);
        }
        if (oldLimitInfoEntity == null) {
            return new LimitationUpdateResponseVo(null, true);
        }

        // 2.查询商品表或者sku表，保存未删除的记录到日志表，以便进行回滚
        List<SkuLimitInfoEntity> skuLimitInfoEntityList = skuLimitInfoDao.listSkuLimitByLimitId(new LimitParam(oldLimitInfoEntity.getPid(), oldLimitInfoEntity.getLimitId()));
        if (CollectionUtils.isEmpty(skuLimitInfoEntityList)) {
            //没有sku记录，查询goods表
            List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitInfoDao.listGoodsLimitByLimitId(new LimitParam(oldLimitInfoEntity.getPid(), oldLimitInfoEntity.getLimitId()));
            buildDeleteLimitationGoodsChangeLog(goodsLimitInfoEntityList, LimitServiceNameEnum.DELETE_ACTIVITY_LIMIT.name(), requestVo);
        } else {
            buildDeleteLimitationSkuChangeLog(skuLimitInfoEntityList, LimitServiceNameEnum.DELETE_ACTIVITY_LIMIT.name(), requestVo);
        }

        // 3.按活动类型进行不同处理
        switch (LimitBizTypeEnum.getLimitLevelEnumByLevel(requestVo.getBizType())) {
            case BIZ_TYPE_DISCOUNT:
                limitationService.deleteDiscountLimitInfo(oldLimitInfoEntity);
                break;
            case BIZ_TYPE_PRIVILEGE_PRICE:
                limitationService.deletePrivilegePriceLimitInfo(oldLimitInfoEntity);
                break;
            case BIZ_TYPE_NYNJ:
                limitationService.deleteNynjLimitInfo(oldLimitInfoEntity);
                break;
            case BIZ_TYPE_COMBINATION_BUY:
                limitationService.deleteCombinationLimitInfo(oldLimitInfoEntity);
                break;
            case BIZ_TYPE_COMMUNITY_GROUPON:
                limitationService.deleteCommunityGrouponLimitInfo(oldLimitInfoEntity,null);
                break;
            default:
                break;
        }
        return new LimitationUpdateResponseVo(oldLimitInfoEntity.getLimitId(), true, LimitContext.getTicket());
    }


    private void buildDeleteLimitationSkuChangeLog(List<SkuLimitInfoEntity> skuLimitInfoEntityList, String name, DeleteLimitationRequestVo requestVo) {
        List<LimitOrderChangeLogEntity> logEntityList = new ArrayList<>();
        for (SkuLimitInfoEntity vo : skuLimitInfoEntityList) {
            LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
            orderChangeLogEntity.setPid(vo.getPid());
            orderChangeLogEntity.setStoreId(vo.getStoreId());
            orderChangeLogEntity.setBizId(requestVo.getBizId());
            orderChangeLogEntity.setBizType(requestVo.getBizType());
            orderChangeLogEntity.setGoodsId(vo.getGoodsId());
            orderChangeLogEntity.setSkuId(vo.getSkuId());
            orderChangeLogEntity.setLimitId(vo.getLimitId());
            orderChangeLogEntity.setTicket(LimitContext.getTicket());
            orderChangeLogEntity.setServiceName(name);
            orderChangeLogEntity.setIsOriginal(LimitConstant.DATA_TYPE_INIT);
            orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
            logEntityList.add(orderChangeLogEntity);
        }
        threadExecutor.execute(new SaveLimitChangeLogThread(limitOrderChangeLogDao, logEntityList, RpcContext.getContext()));
    }

    private void buildDeleteLimitationGoodsChangeLog(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList, String name, DeleteLimitationRequestVo requestVo) {
        if (CollectionUtils.isNotEmpty(goodsLimitInfoEntityList)) {
            List<LimitOrderChangeLogEntity> logEntityList = new ArrayList<>();
            for (GoodsLimitInfoEntity vo : goodsLimitInfoEntityList) {
                LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
                orderChangeLogEntity.setPid(vo.getPid());
                orderChangeLogEntity.setStoreId(vo.getStoreId());
                orderChangeLogEntity.setBizId(requestVo.getBizId());
                orderChangeLogEntity.setBizType(requestVo.getBizType());
                orderChangeLogEntity.setGoodsId(vo.getGoodsId());
                orderChangeLogEntity.setLimitId(vo.getLimitId());
                orderChangeLogEntity.setTicket(LimitContext.getTicket());
                orderChangeLogEntity.setServiceName(name);
                orderChangeLogEntity.setIsOriginal(LimitConstant.DATA_TYPE_INIT);
                orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
                logEntityList.add(orderChangeLogEntity);
            }
            threadExecutor.execute(new SaveLimitChangeLogThread(limitOrderChangeLogDao, logEntityList, RpcContext.getContext()));
        }
    }


    @Override
    public LimitationUpdateResponseVo batchDeleteGoodsLimit(BatchDeleteGoodsLimitRequestVo requestVo) {

        Long pid = requestVo.getDeleteGoodsLimitVoList().get(0).getPid();
        Long bizId = requestVo.getDeleteGoodsLimitVoList().get(0).getBizId();
        Integer bizType = requestVo.getDeleteGoodsLimitVoList().get(0).getBizType();
        Integer activityStockType = null;
        List<Long> goodsIdList = new ArrayList<>();
        List<LimitOrderChangeLogEntity> logEntityList = new ArrayList<>();
        switch (LimitBizTypeEnum.getLimitLevelEnumByLevel(bizType)) {
            case BIZ_TYPE_DISCOUNT:
            case BIZ_TYPE_PRIVILEGE_PRICE:
                LimitInfoEntity oldLimitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(pid, bizId, bizType));
                if (oldLimitInfoEntity == null) {
                    throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
                }
                for (BatchDeleteGoodsLimitVo limitVo : requestVo.getDeleteGoodsLimitVoList()) {
                    activityStockType = limitVo.getActivityStockType();
                    goodsIdList.add(limitVo.getGoodsId());
                    LimitOrderChangeLogEntity limitOrderChangeLogEntity = buildChangeLog(limitVo, LimitServiceNameEnum.DELETE_GOODS_LIMIT.name(), oldLimitInfoEntity.getLimitId());
                    logEntityList.add(limitOrderChangeLogEntity);
                }

                if (Objects.equals(bizType, ActivityTypeEnum.PRIVILEGE_PRICE.getType())
                        || (Objects.equals(bizType, ActivityTypeEnum.DISCOUNT.getType()))
                        && Objects.equals(activityStockType, LimitConstant.DISCOUNT_TYPE_SKU)) {
                    limitationService.deleteSkuLimitInfo(pid, oldLimitInfoEntity.getLimitId(), goodsIdList);
                } else {
                    limitationService.deleteGoodsLimitInfo(pid, oldLimitInfoEntity.getLimitId(), goodsIdList);
                }
                break;
            case BIZ_TYPE_POINT:
                //积分商城,删除主表信息，商品表信息
                for (BatchDeleteGoodsLimitVo limitVo : requestVo.getDeleteGoodsLimitVoList()) {
                    List<Long> pointGoodsIdList = new ArrayList<>();
                    LimitInfoEntity entity = new LimitInfoEntity();
                    entity.setPid(limitVo.getPid());
                    pointGoodsIdList.add(limitVo.getGoodsId());
                    LimitInfoEntity oldPointLimitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(limitVo.getPid(), limitVo.getBizId(), limitVo.getBizType()));
                    if (oldPointLimitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
                    }
                    LimitOrderChangeLogEntity limitOrderChangeLogEntity = buildChangeLog(limitVo, LimitServiceNameEnum.DELETE_GOODS_LIMIT.name(), oldPointLimitInfoEntity.getLimitId());
                    logEntityList.add(limitOrderChangeLogEntity);
                    entity.setLimitId(oldPointLimitInfoEntity.getLimitId());
                    entity.setVersion(oldPointLimitInfoEntity.getVersion());
                    limitationService.deletePointGoodsLimitInfo(entity, pointGoodsIdList);
                }
                break;
            case BIZ_TYPE_COMMUNITY_GROUPON:
                LimitInfoEntity limitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(pid, bizId, bizType));
                if (limitInfoEntity == null) {
                    throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
                }
                for (BatchDeleteGoodsLimitVo limitVo : requestVo.getDeleteGoodsLimitVoList()) {
                    goodsIdList.add(limitVo.getGoodsId());
                    LimitOrderChangeLogEntity limitOrderChangeLogEntity = buildChangeLog(limitVo, LimitServiceNameEnum.DELETE_GOODS_LIMIT.name(), limitInfoEntity.getLimitId());
                    logEntityList.add(limitOrderChangeLogEntity);
                }

                //社区团购删除sku限购信息
                DeleteGoodsParam param = new DeleteGoodsParam();
                param.setPid(pid);
                param.setLimitId(limitInfoEntity.getLimitId());
                param.setGoodsIdList(goodsIdList);
                skuLimitInfoDao.deleteSkuLimitByGoodsId(param);

                break;
            default:
                break;
        }

        threadExecutor.execute(new SaveLimitChangeLogThread(limitOrderChangeLogDao, logEntityList, RpcContext.getContext()));

        return new LimitationUpdateResponseVo(bizId, true, LimitContext.getTicket());
    }

    private LimitOrderChangeLogEntity buildChangeLog(BatchDeleteGoodsLimitVo vo, String name, Long limitId) {
        LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
        orderChangeLogEntity.setPid(vo.getPid());
        orderChangeLogEntity.setStoreId(vo.getStoreId());
        orderChangeLogEntity.setBizId(vo.getBizId());
        orderChangeLogEntity.setBizType(vo.getBizType());
        orderChangeLogEntity.setGoodsId(vo.getGoodsId());
        orderChangeLogEntity.setLimitId(limitId);
        orderChangeLogEntity.setTicket(LimitContext.getTicket());
        orderChangeLogEntity.setServiceName(name);
        orderChangeLogEntity.setIsOriginal(LimitConstant.DATA_TYPE_OVER);
        orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
        return orderChangeLogEntity;
    }

    @Override
    public SaveGoodsLimitInfoResponseVo saveGoodsLimitInfo(SaveGoodsLimitInfoRequestVo saveGoodsLimitInfoRequestVo) {
        Long limitId = null;
        List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = null;
        Long bizId = saveGoodsLimitInfoRequestVo.getGoodsList().get(0).getBizId();
        Integer bizType = saveGoodsLimitInfoRequestVo.getGoodsList().get(0).getBizType();
        Long pid = saveGoodsLimitInfoRequestVo.getGoodsList().get(0).getPid();
        Integer activityStockType = saveGoodsLimitInfoRequestVo.getGoodsList().get(0).getActivityStockType();
        switch (LimitBizTypeEnum.getLimitLevelEnumByLevel(bizType)) {
            case BIZ_TYPE_DISCOUNT:
            case BIZ_TYPE_PRIVILEGE_PRICE:
                //限时折扣,特权价，先查询limitId，再插入限购商品表
                /** 1 查询限购主表信息*/
                LimitInfoEntity oldLimitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(pid, bizId, bizType));
                if (oldLimitInfoEntity == null) {
                    throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
                }
                limitId = oldLimitInfoEntity.getLimitId();
                goodsLimitInfoEntityList = buildGoodsLimitInfoEntity(limitId, saveGoodsLimitInfoRequestVo);
                /** 2 如果是特权价，插入goods、sku限购表*/
                if (Objects.equals(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), bizType)
                        || (Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), bizType)
                        && Objects.equals(activityStockType, LimitConstant.DISCOUNT_TYPE_SKU))) {
                    List<SkuLimitInfoEntity> skuLimitInfoList = buildSkuLimitInfoEntity(limitId, saveGoodsLimitInfoRequestVo);
                    limitationService.addSkuLimitInfoList(skuLimitInfoList, goodsLimitInfoEntityList);
                } else {
                    /** 3 插入商品限购表*/
                    limitationService.addGoodsLimitInfoEntity(goodsLimitInfoEntityList);
                }
                break;
            case BIZ_TYPE_POINT:
                //积分商城商品限购
                /** 1 生成全局id */
                limitId = IdUtils.getLimitId(pid);
                /** 2 构建限购主表信息*/
                LimitInfoEntity limitInfoEntity = buildPointGoodsLimitInfoEntity(limitId, saveGoodsLimitInfoRequestVo.getGoodsList().get(0));
                goodsLimitInfoEntityList = buildGoodsLimitInfoEntity(limitId, saveGoodsLimitInfoRequestVo);
                List<SkuLimitInfoEntity> skuLimitInfoList = buildSkuLimitInfoEntity(limitId, saveGoodsLimitInfoRequestVo);
                limitationService.saveGoodsLimitInfo(limitInfoEntity, goodsLimitInfoEntityList, skuLimitInfoList);
                break;
            case BIZ_TYPE_COMMUNITY_GROUPON:
                // 社区团购SKU限购
                LimitInfoEntity limitInfo = limitInfoDao.getLimitInfo(new LimitParam(pid, bizId, bizType));
                if (limitInfo == null) {
                    throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
                }
                List<SkuLimitInfoEntity> skuLimitInfos = buildSkuLimitInfoEntity(limitInfo.getLimitId(), saveGoodsLimitInfoRequestVo);
                Integer updateResult;
                try {
                    updateResult = skuLimitInfoDao.batchInsertSkuLimit(skuLimitInfos);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_SKU_INFO_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_SKU_INFO_ERROR);
                }
                break;
            default:
                break;
        }

        saveGoodsLimitSaveChangeLog(limitId, LimitServiceNameEnum.SAVE_GOODS_LIMIT.name(), saveGoodsLimitInfoRequestVo);

        return new SaveGoodsLimitInfoResponseVo(true, LimitContext.getTicket());
    }

    private void saveGoodsLimitSaveChangeLog(Long limitId, String name, SaveGoodsLimitInfoRequestVo saveGoodsLimitInfoRequestVo) {
        //异步插入日志，以便于回滚
        List<LimitOrderChangeLogEntity> logEntityList = new ArrayList<>();
        for (SaveGoodsLimitInfoVo vo : saveGoodsLimitInfoRequestVo.getGoodsList()) {
            LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
            orderChangeLogEntity.setPid(vo.getPid());
            orderChangeLogEntity.setStoreId(vo.getStoreId());
            orderChangeLogEntity.setBizId(vo.getBizId());
            orderChangeLogEntity.setBizType(vo.getBizType());
            orderChangeLogEntity.setGoodsId(vo.getGoodsId());
            orderChangeLogEntity.setLimitId(limitId);
            orderChangeLogEntity.setTicket(LimitContext.getTicket());
            orderChangeLogEntity.setServiceName(name);
            orderChangeLogEntity.setIsOriginal(LimitConstant.DATA_TYPE_OVER);
            orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
            logEntityList.add(orderChangeLogEntity);
        }
        threadExecutor.execute(new SaveLimitChangeLogThread(limitOrderChangeLogDao, logEntityList, RpcContext.getContext()));
    }

    @Override
    public SaveGoodsLimitInfoResponseVo updateGoodsLimitInfo(SaveGoodsLimitInfoRequestVo saveGoodsLimitInfoRequestVo) {
        //直接处理限购商品表，需要判断类型决定是否处理sku限购表
        Long bizId = saveGoodsLimitInfoRequestVo.getGoodsList().get(0).getBizId();
        Integer bizType = saveGoodsLimitInfoRequestVo.getGoodsList().get(0).getBizType();
        Long pid = saveGoodsLimitInfoRequestVo.getGoodsList().get(0).getPid();
        Integer activityStockType = saveGoodsLimitInfoRequestVo.getGoodsList().get(0).getActivityStockType();
        /** 1 查询限购主表信息*/
        LimitInfoEntity oldLimitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(pid, bizId, bizType));
        if (oldLimitInfoEntity == null) {
            throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
        }
        Long limitId = oldLimitInfoEntity.getLimitId();

        /** 2 更新商品限购表限购值*/
        List<GoodsLimitInfoEntity> newGoodsLimitInfoEntityList = null;
        List<GoodsLimitInfoEntity> oldGoodsLimitInfoList = null;
        if (!Objects.equals(LimitBizTypeEnum.BIZ_TYPE_COMMUNITY_GROUPON.getLevel(), bizType)) {
            newGoodsLimitInfoEntityList = buildGoodsLimitInfoEntity(limitId, saveGoodsLimitInfoRequestVo);
            oldGoodsLimitInfoList = goodsLimitInfoDao.listGoodsLimitByGoodsId(buildQueryGoodsLimitList(saveGoodsLimitInfoRequestVo, limitId));
        }
        List<SkuLimitInfoEntity> oldSkuLimitInfoList = new ArrayList<>();
        List<SkuLimitInfoEntity> newSkuLimitInfoList = null;
        if (Objects.equals(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), bizType)
                || (Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), bizType)
                && Objects.equals(activityStockType, LimitConstant.DISCOUNT_TYPE_SKU))
                || Objects.equals(LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel(), bizType)
                || Objects.equals(LimitBizTypeEnum.BIZ_TYPE_COMMUNITY_GROUPON.getLevel(), bizType)) {
            /** 3 特权价需要更新商品限购值，删除sku商品记录，插入新的sku商品记录*/
            newSkuLimitInfoList = buildSkuLimitInfoEntity(limitId, saveGoodsLimitInfoRequestVo);
            oldSkuLimitInfoList = limitationService.updatePrivilegePriceGoodsLimitInfo(newGoodsLimitInfoEntityList, newSkuLimitInfoList);
        } else {
            limitationService.updateGoodsLimitInfoEntity(newGoodsLimitInfoEntityList);
        }

        //插入日志
        saveGoodsLimitUpdateChangeLog(oldGoodsLimitInfoList, oldSkuLimitInfoList, LimitServiceNameEnum.UPDATE_GOODS_LIMIT.name(), oldLimitInfoEntity,
                newGoodsLimitInfoEntityList, newSkuLimitInfoList);

        return new SaveGoodsLimitInfoResponseVo(true, LimitContext.getTicket());
    }

    private void saveGoodsLimitUpdateChangeLog(List<GoodsLimitInfoEntity> oldGoodsLimitInfoList, List<SkuLimitInfoEntity> oldSkuLimitInfoList, String serviceName,
                                               LimitInfoEntity oldLimitInfoEntity, List<GoodsLimitInfoEntity> newGoodsLimitInfoEntityList, List<SkuLimitInfoEntity> newSkuLimitInfoList) {
        //异步插入日志，以便于回滚
        List<LimitOrderChangeLogEntity> logEntityList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(oldGoodsLimitInfoList)) {
            for (GoodsLimitInfoEntity vo : oldGoodsLimitInfoList) {
                LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
                orderChangeLogEntity.setPid(vo.getPid());
                orderChangeLogEntity.setStoreId(vo.getStoreId());
                orderChangeLogEntity.setBizId(oldLimitInfoEntity.getBizId());
                orderChangeLogEntity.setBizType(oldLimitInfoEntity.getBizType());
                orderChangeLogEntity.setBuyNum(vo.getLimitNum());
                orderChangeLogEntity.setLimitLevel(vo.getLimitLevel());
                orderChangeLogEntity.setGoodsId(vo.getGoodsId());
                orderChangeLogEntity.setLimitId(vo.getLimitId());
                orderChangeLogEntity.setTicket(LimitContext.getTicket());
                orderChangeLogEntity.setServiceName(serviceName);
                orderChangeLogEntity.setIsOriginal(LimitConstant.DATA_TYPE_INIT);
                orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
                logEntityList.add(orderChangeLogEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(oldSkuLimitInfoList)) {
            for (SkuLimitInfoEntity vo : oldSkuLimitInfoList) {
                LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
                orderChangeLogEntity.setPid(vo.getPid());
                orderChangeLogEntity.setStoreId(vo.getStoreId());
                orderChangeLogEntity.setBizId(oldLimitInfoEntity.getBizId());
                orderChangeLogEntity.setBizType(oldLimitInfoEntity.getBizType());
                orderChangeLogEntity.setBuyNum(vo.getLimitNum());
                orderChangeLogEntity.setGoodsId(vo.getGoodsId());
                orderChangeLogEntity.setSkuId(vo.getSkuId());
                orderChangeLogEntity.setLimitId(vo.getLimitId());
                orderChangeLogEntity.setTicket(LimitContext.getTicket());
                orderChangeLogEntity.setServiceName(serviceName);
                orderChangeLogEntity.setIsOriginal(LimitConstant.DATA_TYPE_INIT);
                orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
                logEntityList.add(orderChangeLogEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(oldGoodsLimitInfoList)) {
            for (GoodsLimitInfoEntity vo : newGoodsLimitInfoEntityList) {
                LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
                orderChangeLogEntity.setPid(vo.getPid());
                orderChangeLogEntity.setStoreId(vo.getStoreId());
                orderChangeLogEntity.setBizId(oldLimitInfoEntity.getBizId());
                orderChangeLogEntity.setBizType(oldLimitInfoEntity.getBizType());
                orderChangeLogEntity.setBuyNum(vo.getLimitNum());
                orderChangeLogEntity.setLimitLevel(vo.getLimitLevel());
                orderChangeLogEntity.setGoodsId(vo.getGoodsId());
                orderChangeLogEntity.setLimitId(vo.getLimitId());
                orderChangeLogEntity.setTicket(LimitContext.getTicket());
                orderChangeLogEntity.setServiceName(serviceName);
                orderChangeLogEntity.setIsOriginal(LimitConstant.DATA_TYPE_OVER);
                orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
                logEntityList.add(orderChangeLogEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(newSkuLimitInfoList)) {
            for (SkuLimitInfoEntity vo : newSkuLimitInfoList) {
                LimitOrderChangeLogEntity orderChangeLogEntity = new LimitOrderChangeLogEntity();
                orderChangeLogEntity.setPid(vo.getPid());
                orderChangeLogEntity.setStoreId(vo.getStoreId());
                orderChangeLogEntity.setBizId(oldLimitInfoEntity.getBizId());
                orderChangeLogEntity.setBizType(oldLimitInfoEntity.getBizType());
                orderChangeLogEntity.setBuyNum(vo.getLimitNum());
                orderChangeLogEntity.setGoodsId(vo.getGoodsId());
                orderChangeLogEntity.setSkuId(vo.getSkuId());
                orderChangeLogEntity.setLimitId(vo.getLimitId());
                orderChangeLogEntity.setTicket(LimitContext.getTicket());
                orderChangeLogEntity.setServiceName(serviceName);
                orderChangeLogEntity.setIsOriginal(LimitConstant.DATA_TYPE_OVER);
                orderChangeLogEntity.setStatus(LimitConstant.ORDER_LOG_STATUS_INIT);
                logEntityList.add(orderChangeLogEntity);
            }
        }
        threadExecutor.execute(new SaveLimitChangeLogThread(limitOrderChangeLogDao, logEntityList, RpcContext.getContext()));
    }

    private List<GoodsLimitInfoEntity> buildQueryGoodsLimitList(SaveGoodsLimitInfoRequestVo saveGoodsLimitInfoRequestVo, Long limitId) {
        List<GoodsLimitInfoEntity> queryGoodsLimitList = new ArrayList<>();
        for (SaveGoodsLimitInfoVo vo : saveGoodsLimitInfoRequestVo.getGoodsList()) {
            GoodsLimitInfoEntity entity = new GoodsLimitInfoEntity();
            entity.setPid(vo.getPid());
            entity.setGoodsId(vo.getGoodsId());
            entity.setLimitId(limitId);
            queryGoodsLimitList.add(entity);
        }
        return queryGoodsLimitList;
    }

    @Override
    public DeleteDiscountUserLimitInfoResponseVo deleteDiscountUserLimitInfo(DeleteDiscountUserLimitInfoRequestVo requestVo) {
        DeleteDiscountUserLimitInfoResponseVo responseVo = new DeleteDiscountUserLimitInfoResponseVo();
        limitationService.deleteDiscountUserLimitInfo(requestVo);
        responseVo.setStatus(true);
        return responseVo;
    }

    private List<SkuLimitInfoEntity> buildSkuLimitInfoEntity(Long limitId, SaveGoodsLimitInfoRequestVo saveGoodsLimitInfoRequestVo) {
        List<SkuLimitInfoEntity> skuLimitInfoEntityList = new ArrayList<>();
        for (SaveGoodsLimitInfoVo requestVo : saveGoodsLimitInfoRequestVo.getGoodsList()) {
            for (SkuLimitInfo info : requestVo.getSkuLimitInfoList()) {
                SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
                skuLimitInfoEntity.setLimitId(limitId);
                skuLimitInfoEntity.setPid(requestVo.getPid());
                skuLimitInfoEntity.setGoodsId(requestVo.getGoodsId());
                skuLimitInfoEntity.setSkuId(info.getSkuId());
                skuLimitInfoEntity.setLimitNum(info.getSkuLimitNum());
                skuLimitInfoEntity.setLimitType(info.getSkuLimitType());
                skuLimitInfoEntityList.add(skuLimitInfoEntity);
            }
        }
        return skuLimitInfoEntityList;
    }

    private List<GoodsLimitInfoEntity> buildGoodsLimitInfoEntity(Long limitId, SaveGoodsLimitInfoRequestVo saveGoodsLimitInfoRequestVo) {
        List<GoodsLimitInfoEntity> infoEntityList = new ArrayList<>();
        for (SaveGoodsLimitInfoVo requestVo : saveGoodsLimitInfoRequestVo.getGoodsList()) {
            GoodsLimitInfoEntity infoEntity = new GoodsLimitInfoEntity();
            infoEntity.setLimitId(limitId);
            infoEntity.setPid(requestVo.getPid());
            infoEntity.setGoodsId(requestVo.getGoodsId());
            infoEntity.setLimitLevel(0);
            infoEntity.setLimitNum(requestVo.getGoodsLimitNum());
            infoEntity.setLimitType(requestVo.getGoodsLimitType());
            infoEntityList.add(infoEntity);

            GoodsLimitInfoEntity infoEntity2 = new GoodsLimitInfoEntity();
            infoEntity2.setLimitId(limitId);
            infoEntity2.setPid(requestVo.getPid());
            infoEntity2.setGoodsId(requestVo.getGoodsId());
            infoEntity2.setLimitLevel(1);
            infoEntity2.setLimitNum(requestVo.getPidGoodsLimitNum() == null ? 0 : requestVo.getPidGoodsLimitNum());
            infoEntity2.setLimitType(requestVo.getGoodsLimitType());
            infoEntityList.add(infoEntity2);
        }
        return infoEntityList;
    }

    private LimitInfoEntity buildPointGoodsLimitInfoEntity(Long limitId, SaveGoodsLimitInfoVo requestVo) {
        LimitInfoEntity limitInfoEntity = new LimitInfoEntity();
        limitInfoEntity.setPid(requestVo.getPid());
        limitInfoEntity.setBizId(requestVo.getBizId());
        limitInfoEntity.setBizType(requestVo.getBizType());
        limitInfoEntity.setLimitId(limitId);
        limitInfoEntity.setLimitLevel(requestVo.getLimitLevel());
        limitInfoEntity.setLimitType(requestVo.getGoodsLimitType());
        limitInfoEntity.setSaleChannelType(requestVo.getChannelType());
        limitInfoEntity.setSource(requestVo.getSource());
        return limitInfoEntity;
    }

    private List<LimitStoreRelationshipEntity> buildStoreInfoList(LimitationInfoRequestVo requestVo) {
        List<LimitStoreRelationshipEntity> storeInfoList = new ArrayList<>();
        for (Long storeId : requestVo.getStoreIdList()) {
            LimitStoreRelationshipEntity entity = new LimitStoreRelationshipEntity();
            entity.setLimitId(requestVo.getLimitId());
            entity.setPid(requestVo.getPid());
            entity.setStoreId(storeId);
            storeInfoList.add(entity);
        }
        return storeInfoList;
    }

    private LimitInfoEntity buildLimitInfoEntity(LimitationInfoRequestVo requestVo) {
        LimitInfoEntity limitInfoEntity = new LimitInfoEntity();
        limitInfoEntity.setLimitId(requestVo.getLimitId());
        limitInfoEntity.setBizId(requestVo.getBizId());
        limitInfoEntity.setBizType(requestVo.getBizType());
        limitInfoEntity.setSaleChannelType(requestVo.getSaleChannelType());
        limitInfoEntity.setLimitLevel(requestVo.getLimitLevel());
        limitInfoEntity.setLimitNum(requestVo.getLimitNum());
        limitInfoEntity.setLimitType(requestVo.getLimitType());
        limitInfoEntity.setPid(requestVo.getPid());
        limitInfoEntity.setSource(requestVo.getSource());
        limitInfoEntity.setSelectStoreType(requestVo.getSelectStoreType());
        return limitInfoEntity;
    }

    private SkuLimitInfoEntity buildSkuInfo(LimitationInfoRequestVo requestVo, Long limitId) {
        SkuLimitInfoEntity skuLimitInfo = new SkuLimitInfoEntity();
        skuLimitInfo.setPid(requestVo.getPid());
        skuLimitInfo.setLimitId(limitId);
        skuLimitInfo.setLimitType(requestVo.getThresholdInfo().getParticularGroupType());
        skuLimitInfo.setGoodsId(requestVo.getBizId());
        skuLimitInfo.setSkuId(requestVo.getBizId());
        skuLimitInfo.setLimitNum(requestVo.getThresholdInfo().getThreshold());
        return skuLimitInfo;
    }

    private void buildAndSaveLimitationInfo(LimitationInfoRequestVo requestVo) {
        LimitInfoEntity limitInfoEntity = buildLimitInfoEntity(requestVo);
        Integer updateResult = 0;
        try {
            updateResult = limitInfoDao.insertLimitInfo(limitInfoEntity);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_LIMIT_INFO_ERROR, e);
        }
        if (updateResult == 0) {
            throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_LIMIT_INFO_ERROR);
        }
    }

    private void buildAndSaveStoreRelationship(LimitationInfoRequestVo requestVo) {
        if (CollectionUtils.isNotEmpty(requestVo.getStoreIdList())) {
            List<LimitStoreRelationshipEntity> storeInfoList = buildStoreInfoList(requestVo);
            Integer updateResult = 0;
            try {
                updateResult = limitStoreRelationshipDao.batchInsertStoreRelationship(storeInfoList);
            } catch (Exception e) {
                throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_STORE_RELATIONSHIP_ERROR, e);
            }
            if (updateResult == 0) {
                throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_STORE_RELATIONSHIP_ERROR);
            }
        }
    }

    private void buildAndSaveSkuInfo(LimitationInfoRequestVo requestVo) {
        if (Objects.equals(requestVo.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
            SkuLimitInfoEntity skuLimitInfo = buildSkuInfo(requestVo, requestVo.getLimitId());
            Integer updateResult = 0;
            try {
                updateResult = skuLimitInfoDao.batchInsertSkuLimit(Arrays.asList(skuLimitInfo));
            } catch (Exception e) {
                throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_SKU_INFO_ERROR, e);
            }
            if (updateResult == 0) {
                throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_SKU_INFO_ERROR);
            }
        }
    }

    private void buildAndUpdateSkuInfo(LimitationInfoRequestVo requestVo, LimitInfoEntity oldLimitInfoEntity) {
        if (Objects.equals(requestVo.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
            SkuLimitInfoEntity skuLimitInfo = buildSkuInfo(requestVo, oldLimitInfoEntity.getLimitId());
            Integer updateResult = 0;
            try {
                updateResult = skuLimitInfoDao.updateSkuLimitNum(skuLimitInfo);
            } catch (Exception e) {
                throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_LIMIT_NUM_ERROR, e);
            }
            if (updateResult == 0) {
                throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_LIMIT_NUM_ERROR);
            }
        }
    }

    private void buildAndUpdateStoreRelationship(LimitationInfoRequestVo requestVo, LimitInfoEntity limitInfoEntity) {
        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(limitInfoEntity.getPid());
        deleteEntity.setLimitId(limitInfoEntity.getLimitId());
        try {
            limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_DELETE_STORE_RELATIONSHIP_ERROR, e);
        }
        buildAndSaveStoreRelationship(requestVo);
    }

    private LimitInfoEntity buildAndUpdateLimitationInfo(LimitationInfoRequestVo requestVo, LimitInfoEntity oldLimitInfoEntity) {
        LimitInfoEntity limitInfoEntity = buildLimitInfoEntity(requestVo);
        limitInfoEntity.setVersion(oldLimitInfoEntity.getVersion());
        Integer updateResult = 0;
        try {
            updateResult = limitInfoDao.updateLimitInfo(limitInfoEntity);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_LIMIT_INFO_ERROR, e);
        }
        if (updateResult == 0) {
            throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_LIMIT_INFO_ERROR);
        }
        return limitInfoEntity;
    }

    private LimitInfoEntity getLimitationInfo(LimitationInfoRequestVo requestVo) {
        LimitInfoEntity oldLimitInfoEntity = null;
        try {
            oldLimitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_LIMIT_INFO_ERROR, e);
        }
        if (oldLimitInfoEntity == null) {
            throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
        }
        requestVo.setLimitId(oldLimitInfoEntity.getLimitId());
        return oldLimitInfoEntity;
    }
}
