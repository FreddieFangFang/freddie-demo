package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
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
import com.weimob.saas.ec.limitation.model.DeleteSkuParam;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.DeleteDiscountUserLimitInfoRequestVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            limitStoreRelationshipDao.batchInsert(storeInfoList);
        }
    }

    public void updateLimitationInfo(LimitInfoEntity limitInfoEntity, List<LimitStoreRelationshipEntity> storeInfoList) {

        limitInfoDao.update(limitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(limitInfoEntity.getPid());
        deleteEntity.setLimitId(limitInfoEntity.getLimitId());
        limitStoreRelationshipDao.delete(deleteEntity);

        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            limitStoreRelationshipDao.batchInsert(storeInfoList);
        }
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
        deleteGoodsLimitInfo(pid, limitId, goodsIdList);
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

    public void saveGoodsLimitInfo(LimitInfoEntity limitInfoEntity, List<GoodsLimitInfoEntity> goodsLimitInfoEntity, List<SkuLimitInfoEntity> skuLimitInfoList) {
        limitInfoDao.insert(limitInfoEntity);
        goodsLimitInfoDao.batchInsert(goodsLimitInfoEntity);
        skuLimitInfoDao.batchInsert(skuLimitInfoList);
    }

    public void addGoodsLimitInfoEntity(List<GoodsLimitInfoEntity> goodsLimitInfoEntity) {
        goodsLimitInfoDao.batchInsert(goodsLimitInfoEntity);
    }

    public void addSkuLimitInfoList(List<SkuLimitInfoEntity> skuLimitInfoList, List<GoodsLimitInfoEntity> goodsLimitInfoEntity) {
        goodsLimitInfoDao.batchInsert(goodsLimitInfoEntity);
        skuLimitInfoDao.batchInsert(skuLimitInfoList);
    }

    public void updateGoodsLimitInfoEntity(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList) {
        for (GoodsLimitInfoEntity goodsLimitInfoEntity : goodsLimitInfoEntityList) {
            goodsLimitInfoDao.updateGoodsLimitInfoEntity(goodsLimitInfoEntity);
        }
    }

    public void deleteDiscountLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.delete(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());

        limitStoreRelationshipDao.delete(deleteEntity);

        deleteSkuLimitInfo(oldLimitInfoEntity.getPid(), oldLimitInfoEntity.getLimitId(), null);
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

        deleteSkuLimitInfo(entity.getPid(), entity.getLimitId(), pointGoodsIdList);
    }

    public List<SkuLimitInfoEntity> updatePrivilegePriceGoodsLimitInfo(List<GoodsLimitInfoEntity> oldGoodsLimitInfoEntityList, List<SkuLimitInfoEntity> skuLimitInfoList) {

        for (GoodsLimitInfoEntity oldGoodsLimitInfoEntity : oldGoodsLimitInfoEntityList) {
            goodsLimitInfoDao.updateGoodsLimitInfoEntity(oldGoodsLimitInfoEntity);
        }

        /**
         * 1.原来有，现在有，更新。
         * 2.原来有，现在没有，删除。
         * 3.原来没有，现在有，新增
         */
        if (CollectionUtils.isEmpty(skuLimitInfoList)) {
            return new ArrayList<>(0);
        }
        Map<Long, List<SkuLimitInfoEntity>> goodsSkuMap = new HashMap<>();
        for (SkuLimitInfoEntity skuLimitInfoEntity : skuLimitInfoList) {
            if (CollectionUtils.isEmpty(goodsSkuMap.get(skuLimitInfoEntity.getGoodsId()))) {
                List<SkuLimitInfoEntity> skuLimitInfoEntityList = new ArrayList<>();
                skuLimitInfoEntityList.add(skuLimitInfoEntity);
                goodsSkuMap.put(skuLimitInfoEntity.getGoodsId(), skuLimitInfoEntityList);
            } else {
                goodsSkuMap.get(skuLimitInfoEntity.getGoodsId()).add(skuLimitInfoEntity);
            }
        }
        Iterator<Map.Entry<Long, List<SkuLimitInfoEntity>>> iterator = goodsSkuMap.entrySet().iterator();
        Long pid = oldGoodsLimitInfoEntityList.get(0).getPid();
        Long limitId = oldGoodsLimitInfoEntityList.get(0).getLimitId();
        DeleteGoodsParam limitParam = new DeleteGoodsParam();
        limitParam.setPid(pid);
        limitParam.setLimitId(limitId);
        limitParam.setGoodsIdList(new ArrayList<>(goodsSkuMap.keySet()));
        List<SkuLimitInfoEntity> oldSkuLimitInfoList = skuLimitInfoDao.queryOldSkuLimitList(limitParam);
        while (iterator.hasNext()) {
            Map.Entry<Long, List<SkuLimitInfoEntity>> entry = iterator.next();
            Long goodsId = entry.getKey();
            List<SkuLimitInfoEntity> skuLimitInfoEntityList = entry.getValue();
            List<Long> skuIdList = new ArrayList<>();
            List<SkuLimitInfoEntity> newSkuLimitList = new ArrayList<>();
            for (SkuLimitInfoEntity skuLimitInfoEntity : oldSkuLimitInfoList) {
                skuIdList.add(skuLimitInfoEntity.getSkuId());
            }
            for (SkuLimitInfoEntity skuLimitInfoEntity : skuLimitInfoEntityList) {
                if (skuIdList.contains(skuLimitInfoEntity.getSkuId())) {
                    //有此记录，进行更新，把skuIdList的id删除
                    skuLimitInfoDao.updateSkuLimitNum(skuLimitInfoEntity);
                    skuIdList.remove(skuLimitInfoEntity.getSkuId());
                } else {
                    //无此记录，进行插入
                    newSkuLimitList.add(skuLimitInfoEntity);
                }
            }
            //skuIdList还存在的skuId要删除
            if (CollectionUtils.isNotEmpty(skuIdList)) {
                DeleteSkuParam param = new DeleteSkuParam();
                param.setPid(pid);
                param.setLimitId(limitId);
                param.setSkuIdList(skuIdList);
                param.setGoodsId(goodsId);
                skuLimitInfoDao.deleteGoodsSkuLimit(param);
            }
            if (CollectionUtils.isNotEmpty(newSkuLimitList)) {
                skuLimitInfoDao.batchInsert(newSkuLimitList);
            }
        }
        return oldSkuLimitInfoList;
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
                    updateResult = userLimitDao.deductUserLimit(activityLimitEntity);
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
                    updateResult = skuLimitInfoDao.deductSkuLimit(activitySoldEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_LIMIT_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_LIMIT_ERROR);
                }
            }
        }
    }

    public void deleteDiscountUserLimitInfo(DeleteDiscountUserLimitInfoRequestVo requestVo) {
        LimitInfoEntity limitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));
        if (limitInfoEntity != null) {
            userLimitDao.deleteDiscountUserLimitInfo(requestVo);
            userGoodsLimitDao.deleteDiscountUserLimitInfo(new LimitParam(requestVo.getPid(), limitInfoEntity.getLimitId()));
        } else {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_ACTIVITY_IS_NULL);
        }
    }

    public void reverseUpdateGoodsLimit(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList, List<SkuLimitInfoEntity> updateSkuList, List<SkuLimitInfoEntity> insertSkuList, List<SkuLimitInfoEntity> deleteSkuList) {
        for (GoodsLimitInfoEntity oldGoodsLimitInfoEntity : goodsLimitInfoEntityList) {
            goodsLimitInfoDao.updateGoodsLimitInfoEntity(oldGoodsLimitInfoEntity);
        }

        if (CollectionUtils.isNotEmpty(updateSkuList)) {
            for (SkuLimitInfoEntity skuLimitInfoEntity : updateSkuList) {
                skuLimitInfoDao.updateSkuLimitNum(skuLimitInfoEntity);
            }
        }

        if (CollectionUtils.isNotEmpty(deleteSkuList)) {
            skuLimitInfoDao.deleteAllGoodsSku(deleteSkuList);
        }

        if (CollectionUtils.isNotEmpty(insertSkuList)) {
            skuLimitInfoDao.updateSkuStatus(insertSkuList);
        }
    }

    public void reverseSaveGoodsLimit(LimitInfoEntity entity, List<Long> goodsList) {
        //积分商城回滚limit_info表
        if (Objects.equals(LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel(), entity.getBizType())) {
            limitInfoDao.delete(entity);
        }

        deleteSkuLimitInfo(entity.getPid(), entity.getLimitId(), goodsList);
    }
}
