package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.dao.*;
import com.weimob.saas.ec.limitation.entity.*;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lujialin
 * @description 操作dao层插入数据
 * @date 2018/5/29 13:49
 */
@Service(value = "limitationService")
public class LimitationServiceImpl {

    @Autowired
    private LimitInfoDao limitInfoDao;
    @Autowired
    private LimitStoreRelationshipDao limitStoreRelationshipDao;
    @Autowired
    private GoodsLimitInfoDao goodsLimitInfoDao;
    @Autowired
    private SkuLimitInfoDao skuLimitInfoDao;
    @Autowired
    private UserGoodsLimitDao userGoodsLimitDao;
    @Autowired
    private UserLimitDao userLimitDao;

    public void saveLimitationInfo(LimitInfoEntity limitInfoEntity, List<LimitStoreRelationshipEntity> storeInfoList) {
        limitInfoDao.insert(limitInfoEntity);
        limitStoreRelationshipDao.batchInsert(storeInfoList);
    }

    public void updateLimitationInfo(LimitInfoEntity limitInfoEntity, List<LimitStoreRelationshipEntity> storeInfoList) {

        limitInfoDao.update(limitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(limitInfoEntity.getPid());
        deleteEntity.setLimitId(limitInfoEntity.getLimitId());
        limitStoreRelationshipDao.delete(deleteEntity);

        limitStoreRelationshipDao.batchInsert(storeInfoList);
    }

    public void deleteLimitInfo(LimitInfoEntity limitInfoEntity) {
        limitInfoDao.delete(limitInfoEntity);
    }

    public void deleteStoreInfoList(Long pid, Long limitId) {
        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(pid);
        deleteEntity.setLimitId(limitId);
        limitStoreRelationshipDao.delete(deleteEntity);
    }

    public void deleteGoodsLimitInfo(Long pid, Long limitId, List<Long> goodsIdList) {
        DeleteGoodsParam param = new DeleteGoodsParam();
        param.setPid(pid);
        param.setLimitId(limitId);
        param.setGoodsIdList(goodsIdList);
        if (CollectionUtils.isEmpty(goodsIdList)) {
            goodsLimitInfoDao.deleteLimit(param);
        } else {
            goodsLimitInfoDao.deleteGoodsLimit(param);
        }

    }

    public void deleteSkuLimitInfo(Long pid, Long limitId, List<Long> goodsIdList) {
        deleteGoodsLimitInfo(pid, limitId, null);
        DeleteGoodsParam param = new DeleteGoodsParam();
        param.setPid(pid);
        param.setLimitId(limitId);
        param.setGoodsIdList(goodsIdList);
        if (CollectionUtils.isEmpty(goodsIdList)) {
            skuLimitInfoDao.deleteLimit(param);
        } else {
            skuLimitInfoDao.deleteSkuLimit(param);
        }
    }

    public void saveGoodsLimitInfo(LimitInfoEntity limitInfoEntity, GoodsLimitInfoEntity goodsLimitInfoEntity) {
        limitInfoDao.insert(limitInfoEntity);
        goodsLimitInfoDao.insert(goodsLimitInfoEntity);
    }

    public void addGoodsLimitInfoEntity(GoodsLimitInfoEntity goodsLimitInfoEntity) {
        goodsLimitInfoDao.insert(goodsLimitInfoEntity);
    }

    public void addSkuLimitInfoList(List<SkuLimitInfoEntity> skuLimitInfoList, GoodsLimitInfoEntity goodsLimitInfoEntity) {
        goodsLimitInfoDao.insert(goodsLimitInfoEntity);
        skuLimitInfoDao.batchInsert(skuLimitInfoList);
    }

    public void updateGoodsLimitInfoEntity(GoodsLimitInfoEntity goodsLimitInfoEntity) {
        goodsLimitInfoDao.updateGoodsLimitInfoEntity(goodsLimitInfoEntity);
    }

    public void deleteDiscountLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.delete(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());

        limitStoreRelationshipDao.delete(deleteEntity);

        deleteGoodsLimitInfo(oldLimitInfoEntity.getPid(), oldLimitInfoEntity.getLimitId(), null);
    }

    public void deletePrivilegePriceLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.delete(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());
        limitStoreRelationshipDao.delete(deleteEntity);

        deleteSkuLimitInfo(oldLimitInfoEntity.getPid(), oldLimitInfoEntity.getLimitId(), null);
    }

    public void deleteNynjLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.delete(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());
        limitStoreRelationshipDao.delete(deleteEntity);
    }

    public void deleteCombinationLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.delete(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());
        limitStoreRelationshipDao.delete(deleteEntity);
    }

    public void deletePointGoodsLimitInfo(LimitInfoEntity entity, List<Long> pointGoodsIdList) {
        limitInfoDao.delete(entity);

        deleteGoodsLimitInfo(entity.getPid(), entity.getLimitId(), pointGoodsIdList);
    }

    public void updatePrivilegePriceGoodsLimitInfo(GoodsLimitInfoEntity oldGoodsLimitInfoEntity, List<SkuLimitInfoEntity> skuLimitInfoList) {

        goodsLimitInfoDao.updateGoodsLimitInfoEntity(oldGoodsLimitInfoEntity);


        DeleteGoodsParam param = new DeleteGoodsParam();
        param.setPid(oldGoodsLimitInfoEntity.getPid());
        param.setLimitId(oldGoodsLimitInfoEntity.getLimitId());
        List<Long> goodsIdList = new ArrayList<>();
        goodsIdList.add(oldGoodsLimitInfoEntity.getGoodsId());
        param.setGoodsIdList(goodsIdList);
        skuLimitInfoDao.deleteSkuLimit(param);

        skuLimitInfoDao.batchInsert(skuLimitInfoList);
    }

    public void saveUserLimitRecode(List<UserGoodsLimitEntity> goodsLimitEntityList, List<UserLimitEntity> activityLimitEntityList,
                                    List<SkuLimitInfoEntity> activityGoodsSoldEntityList,
                                    List<UserLimitEntity> activityLimitRecodeList,
                                    List<UserGoodsLimitEntity> userGoodsLimitRecodeList, Boolean update) {

        Long updateResult = 0l;
        if (!org.springframework.util.CollectionUtils.isEmpty(goodsLimitEntityList)) {
            for (UserGoodsLimitEntity goodsLimitEntity : goodsLimitEntityList) {
                try {
                    if (!update && org.springframework.util.CollectionUtils.isEmpty(userGoodsLimitRecodeList)) {
                        updateResult = userGoodsLimitDao.insert(goodsLimitEntity);
                    } else {
                        updateResult = userGoodsLimitDao.update(goodsLimitEntity);
                    }
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_ERROR);
                }
            }
        }

        if (!org.springframework.util.CollectionUtils.isEmpty(activityLimitEntityList)) {
            for (UserLimitEntity activityLimitEntity : activityLimitEntityList) {
                try {
                    if (!update && org.springframework.util.CollectionUtils.isEmpty(activityLimitRecodeList)) {
                        updateResult = userLimitDao.insert(activityLimitEntity);
                    } else {
                        updateResult = userLimitDao.update(activityLimitEntity);
                    }
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_LIMIT_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_LIMIT_ERROR);
                }
            }
        }

        if (!org.springframework.util.CollectionUtils.isEmpty(activityGoodsSoldEntityList)) {
            for (SkuLimitInfoEntity activitySoldEntity : activityGoodsSoldEntityList) {
                try {
                    updateResult = skuLimitInfoDao.update(activitySoldEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_LIMIT_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_LIMIT_ERROR);
                }
            }
        }

    }
}
