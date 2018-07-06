package com.weimob.saas.ec.limitation.service.impl;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.DeleteDiscountUserLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.model.response.SaveGoodsLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.service.LimitationUpdateBizService;
import com.weimob.saas.ec.limitation.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private LimitInfoDao limitInfoDao;


    @Override
    public LimitationUpdateResponseVo saveLimitationInfo(LimitationInfoRequestVo requestVo) {
        /** 1 生成全局id */
        Long limitId = IdUtils.getLimitId(requestVo.getPid());
        requestVo.setLimitId(limitId);
        /** 2 构建限购主表信息*/
        LimitInfoEntity limitInfoEntity = buildLimitInfoEntity(requestVo);
        /** 3 构建限购门店表信息*/
        List<LimitStoreRelationshipEntity> storeInfoList = buildStoreInfoList(requestVo);
        /** 4 保存数据库*/
        limitationService.saveLimitationInfo(limitInfoEntity, storeInfoList);

        return new LimitationUpdateResponseVo(limitId, true);
    }

    @Override
    public LimitationUpdateResponseVo updateLimitationInfo(LimitationInfoRequestVo requestVo) {
        /** 1 查询限购主表信息*/
        LimitInfoEntity oldLimitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));

        if (oldLimitInfoEntity == null) {
            throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
        }
        requestVo.setLimitId(oldLimitInfoEntity.getLimitId());
        /** 2 构建限购主表更新信息*/
        LimitInfoEntity limitInfoEntity = buildLimitInfoEntity(requestVo);
        limitInfoEntity.setVersion(oldLimitInfoEntity.getVersion());
        /** 3 构建限购门店表信息*/
        List<LimitStoreRelationshipEntity> storeInfoList = buildStoreInfoList(requestVo);
        /** 4 更新数据库*/
        limitationService.updateLimitationInfo(limitInfoEntity, storeInfoList);

        return new LimitationUpdateResponseVo(oldLimitInfoEntity.getLimitId(), true);
    }

    @Override
    public LimitationUpdateResponseVo deleteLimitationInfo(DeleteLimitationRequestVo requestVo) {
        /** 1 查询限购主表信息*/
        LimitInfoEntity oldLimitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));

        if (oldLimitInfoEntity == null) {
            throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
        }
        Long pid = requestVo.getPid();
        Long limitId = oldLimitInfoEntity.getLimitId();
        switch (requestVo.getBizType()) {
            case 3:
                limitationService.deleteDiscountLimitInfo(oldLimitInfoEntity);
                break;
            case 10:
                limitationService.deletePrivilegePriceLimitInfo(oldLimitInfoEntity);
                break;
            case 12:
                limitationService.deleteNynjLimitInfo(oldLimitInfoEntity);
                break;
            case 13:
                limitationService.deleteCombinationLimitInfo(oldLimitInfoEntity);
                break;
            case 14:
                break;
            default:
                break;
        }

        return new LimitationUpdateResponseVo(oldLimitInfoEntity.getLimitId(), true);
    }

    @Override
    public LimitationUpdateResponseVo batchDeleteGoodsLimit(BatchDeleteGoodsLimitRequestVo requestVo) {

        Long pid = null;
        Long bizId = null;
        Integer bizType = null;
        List<Long> goodsIdList = new ArrayList<>();
        switch (requestVo.getDeleteGoodsLimitVoList().get(0).getBizType()) {
            case 3:
            case 10:
                for (BatchDeleteGoodsLimitVo limitVo : requestVo.getDeleteGoodsLimitVoList()) {
                    pid = limitVo.getPid();
                    bizId = limitVo.getBizId();
                    bizType = limitVo.getBizType();
                    goodsIdList.add(limitVo.getGoodsId());
                }
                LimitInfoEntity oldLimitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(pid, bizId, bizType));
                if (oldLimitInfoEntity == null) {
                    throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
                }
                if (Objects.equals(bizType, ActivityTypeEnum.PRIVILEGE_PRICE.getType())) {
                    limitationService.deleteSkuLimitInfo(pid, oldLimitInfoEntity.getLimitId(), goodsIdList);
                } else {
                    limitationService.deleteGoodsLimitInfo(pid, oldLimitInfoEntity.getLimitId(), goodsIdList);
                }
                break;
            case 30:
                //积分商城,删除主表信息，商品表信息
                for (BatchDeleteGoodsLimitVo limitVo : requestVo.getDeleteGoodsLimitVoList()) {
                    List<Long> pointGoodsIdList = new ArrayList<>();
                    LimitInfoEntity entity = new LimitInfoEntity();
                    entity.setPid(limitVo.getPid());
                    pointGoodsIdList.add(limitVo.getGoodsId());
                    LimitInfoEntity oldPointLimitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(limitVo.getPid(), limitVo.getBizId(), limitVo.getBizType()));
                    if (oldPointLimitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
                    }
                    entity.setLimitId(oldPointLimitInfoEntity.getLimitId());
                    limitationService.deletePointGoodsLimitInfo(entity, pointGoodsIdList);
                }
                break;
            default:
                break;
        }

        return new LimitationUpdateResponseVo(bizId, true);
    }

    @Override
    public SaveGoodsLimitInfoResponseVo saveGoodsLimitInfo(SaveGoodsLimitInfoRequestVo saveGoodsLimitInfoRequestVo) {
        for (SaveGoodsLimitInfoVo requestVo : saveGoodsLimitInfoRequestVo.getGoodsList()) {
            Long limitId = null;
            GoodsLimitInfoEntity goodsLimitInfoEntity = null;
            switch (requestVo.getBizType()) {
                case 3:
                case 10:
                    //限时折扣,特权价，先查询limitId，再插入限购商品表
                    /** 1 查询限购主表信息*/
                    LimitInfoEntity oldLimitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));
                    if (oldLimitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
                    }
                    limitId = oldLimitInfoEntity.getLimitId();
                    goodsLimitInfoEntity = buildGoodsLimitInfoEntity(limitId, requestVo);
                    /** 2 如果是特权价，插入goods、sku限购表*/
                    if (Objects.equals(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), requestVo.getBizType())) {
                        List<SkuLimitInfoEntity> skuLimitInfoList = buildSkuLimitInfoEntity(limitId, requestVo);
                        limitationService.addSkuLimitInfoList(skuLimitInfoList, goodsLimitInfoEntity);
                    } else {
                        /** 3 插入商品限购表*/
                        limitationService.addGoodsLimitInfoEntity(goodsLimitInfoEntity);
                    }
                    break;
                case 30:
                    //积分商城商品限购
                    /** 1 生成全局id */
                    limitId = IdUtils.getLimitId(requestVo.getPid());
                    /** 2 构建限购主表信息*/
                    LimitInfoEntity limitInfoEntity = buildPointGoodsLimitInfoEntity(limitId, requestVo);
                    goodsLimitInfoEntity = buildGoodsLimitInfoEntity(limitId, requestVo);
                    limitationService.saveGoodsLimitInfo(limitInfoEntity, goodsLimitInfoEntity);
                    break;
                default:
                    break;
            }

        }
        return new SaveGoodsLimitInfoResponseVo(true, null);
    }

    @Override
    public SaveGoodsLimitInfoResponseVo updateGoodsLimitInfo(SaveGoodsLimitInfoRequestVo saveGoodsLimitInfoRequestVo) {
        for (SaveGoodsLimitInfoVo requestVo : saveGoodsLimitInfoRequestVo.getGoodsList()) {
            //直接处理限购商品表，需要判断类型决定是否处理sku限购表
            /** 1 查询限购主表信息*/
            LimitInfoEntity oldLimitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));
            if (oldLimitInfoEntity == null) {
                throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
            }
            Long limitId = oldLimitInfoEntity.getLimitId();

            /** 2 更新商品限购表限购值*/
            GoodsLimitInfoEntity oldGoodsLimitInfoEntity = buildGoodsLimitInfoEntity(limitId, requestVo);

            if (Objects.equals(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), requestVo.getBizType())) {
                /** 3 特权价需要更新商品限购值，删除sku商品记录，插入新的sku商品记录*/
                List<SkuLimitInfoEntity> skuLimitInfoList = buildSkuLimitInfoEntity(limitId, requestVo);
                limitationService.updatePrivilegePriceGoodsLimitInfo(oldGoodsLimitInfoEntity, skuLimitInfoList);

            } else {
                limitationService.updateGoodsLimitInfoEntity(oldGoodsLimitInfoEntity);
            }
        }

        return new SaveGoodsLimitInfoResponseVo(true, null);
    }

    @Override
    public DeleteDiscountUserLimitInfoResponseVo deleteDiscountUserLimitInfo(DeleteDiscountUserLimitInfoRequestVo requestVo) {
        DeleteDiscountUserLimitInfoResponseVo responseVo = new DeleteDiscountUserLimitInfoResponseVo();
        limitationService.deleteDiscountUserLimitInfo(requestVo);
        responseVo.setStatus(true);
        return responseVo;
    }

    private List<SkuLimitInfoEntity> buildSkuLimitInfoEntity(Long limitId, SaveGoodsLimitInfoVo requestVo) {
        List<SkuLimitInfoEntity> skuLimitInfoEntityList = new ArrayList<>();
        for (SkuLimitInfo info : requestVo.getSkuLimitInfoList()) {
            SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
            skuLimitInfoEntity.setLimitId(limitId);
            skuLimitInfoEntity.setPid(requestVo.getPid());
            skuLimitInfoEntity.setStoreId(requestVo.getStoreId());
            skuLimitInfoEntity.setGoodsId(requestVo.getGoodsId());
            skuLimitInfoEntity.setSkuId(info.getSkuId());
            skuLimitInfoEntity.setLimitNum(info.getSkuLimitNum());
            skuLimitInfoEntity.setLimitType(info.getSkuLimitType());
            skuLimitInfoEntityList.add(skuLimitInfoEntity);
        }
        return skuLimitInfoEntityList;
    }

    private GoodsLimitInfoEntity buildGoodsLimitInfoEntity(Long limitId, SaveGoodsLimitInfoVo requestVo) {
        GoodsLimitInfoEntity infoEntity = new GoodsLimitInfoEntity();
        infoEntity.setLimitId(limitId);
        infoEntity.setPid(requestVo.getPid());
        infoEntity.setStoreId(requestVo.getStoreId());
        infoEntity.setGoodsId(requestVo.getGoodsId());
        infoEntity.setLimitNum(requestVo.getGoodsLimitNum());
        infoEntity.setLimitType(requestVo.getGoodsLimitType());
        return infoEntity;
    }

    private LimitInfoEntity buildPointGoodsLimitInfoEntity(Long limitId, SaveGoodsLimitInfoVo requestVo) {
        LimitInfoEntity limitInfoEntity = new LimitInfoEntity();
        limitInfoEntity.setPid(requestVo.getPid());
        limitInfoEntity.setBizId(requestVo.getBizId());
        limitInfoEntity.setBizType(requestVo.getBizType());
        limitInfoEntity.setLimitId(limitId);
        limitInfoEntity.setLimitLevel(requestVo.getLimitLevel());
        limitInfoEntity.setLimitType(requestVo.getGoodsLimitType());
        limitInfoEntity.setChannelType(requestVo.getChannelType());
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
        limitInfoEntity.setChannelType(requestVo.getChannelType());
        limitInfoEntity.setLimitLevel(requestVo.getLimitLevel());
        limitInfoEntity.setLimitNum(requestVo.getLimitNum());
        limitInfoEntity.setLimitType(requestVo.getLimitType());
        limitInfoEntity.setPid(requestVo.getPid());
        limitInfoEntity.setSource(requestVo.getSource());
        return limitInfoEntity;
    }
}
