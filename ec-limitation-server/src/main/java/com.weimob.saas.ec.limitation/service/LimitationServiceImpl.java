package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.dao.GoodsLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitStoreRelationshipDao;
import com.weimob.saas.ec.limitation.dao.SkuLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.UserGoodsLimitDao;
import com.weimob.saas.ec.limitation.dao.UserLimitDao;
import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public void saveUserLimitRecord(List<UserGoodsLimitEntity> goodsLimitEntityList, List<UserLimitEntity> activityLimitEntityList,
                                    List<SkuLimitInfoEntity> activityGoodsSoldEntityList) {

        Long updateResult = 0l;
        if (CollectionUtils.isNotEmpty(goodsLimitEntityList)) {
            for (UserGoodsLimitEntity goodsLimitEntity : goodsLimitEntityList) {
                try {
                    UserGoodsLimitEntity oldUserGoodsLimitEntity = userGoodsLimitDao.getUserGoodsLimitEntity(goodsLimitEntity);
                    if (oldUserGoodsLimitEntity == null) {
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

        if (CollectionUtils.isNotEmpty(activityLimitEntityList)) {
            for (UserLimitEntity activityLimitEntity : activityLimitEntityList) {
                try {
                    UserLimitEntity oldUserLimitEntity = userLimitDao.getUserLimitEntity(activityLimitEntity);
                    if (oldUserLimitEntity == null) {
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

        if (CollectionUtils.isNotEmpty(activityGoodsSoldEntityList)) {
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

    /**
     * 修改限购记录
     *
     * @param goodsLimitEntityList
     * @param activityLimitEntityList
     * @param activityGoodsSoldEntityList
     * @scenes 取消下单
     * @author Pengqin ZHOU
     * @date 2018/6/8
     */
    public void updateUserLimitRecord(List<UserGoodsLimitEntity> goodsLimitEntityList, List<UserLimitEntity> activityLimitEntityList,
                                      List<SkuLimitInfoEntity> activityGoodsSoldEntityList) {

        Integer updateResult = 0;
        if (!CollectionUtils.isEmpty(goodsLimitEntityList)) {
            for (UserGoodsLimitEntity goodsLimitEntity : goodsLimitEntityList) {
                try {
                    updateResult = userGoodsLimitDao.deductUserGoodsLimit(goodsLimitEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_ERROR, e);
                }
                if (updateResult.equals(0)) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_ERROR);
                }
            }
        }

        if (!CollectionUtils.isEmpty(activityLimitEntityList)) {
            for (UserLimitEntity activityLimitEntity : activityLimitEntityList) {
                try {
                    // TODO
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_LIMIT_ERROR, e);
                }
                if (updateResult.equals(0)) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_LIMIT_ERROR);
                }
            }
        }

        if (!CollectionUtils.isEmpty(activityGoodsSoldEntityList)) {
            for (SkuLimitInfoEntity activitySoldEntity : activityGoodsSoldEntityList) {
                try {
                    // TODO
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
