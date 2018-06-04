package com.weimob.saas.ec.limitation.service.impl;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.BatchDeleteGoodsLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.BatchDeleteGoodsLimitVo;
import com.weimob.saas.ec.limitation.model.request.DeleteLimitationRequestVo;
import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
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
                limitationService.deleteLimitInfo(oldLimitInfoEntity);
                limitationService.deleteStoreInfoList(pid, limitId);
                limitationService.deleteGoodsLimitInfo(pid, limitId, null);
                break;
            case 10:
                limitationService.deleteLimitInfo(oldLimitInfoEntity);
                limitationService.deleteStoreInfoList(pid, limitId);
                limitationService.deleteGoodsLimitInfo(pid, limitId, null);
                limitationService.deleteSkuLimitInfo(pid, limitId, null);
                break;
            case 12:
                limitationService.deleteLimitInfo(oldLimitInfoEntity);
                limitationService.deleteStoreInfoList(pid, limitId);
                break;
            case 13:
                limitationService.deleteLimitInfo(oldLimitInfoEntity);
                limitationService.deleteStoreInfoList(pid, limitId);
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
                limitationService.deleteGoodsLimitInfo(pid, oldLimitInfoEntity.getLimitId(), goodsIdList);
                if (Objects.equals(bizType, ActivityTypeEnum.PRIVILEGE_PRICE.getType())) {
                    limitationService.deleteSkuLimitInfo(pid, oldLimitInfoEntity.getLimitId(), goodsIdList);
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
                    limitationService.deleteLimitInfo(entity);
                    limitationService.deleteGoodsLimitInfo(limitVo.getPid(), oldPointLimitInfoEntity.getLimitId(), pointGoodsIdList);
                }
                break;
            default:
                break;
        }

        return new LimitationUpdateResponseVo(bizId, true);
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
