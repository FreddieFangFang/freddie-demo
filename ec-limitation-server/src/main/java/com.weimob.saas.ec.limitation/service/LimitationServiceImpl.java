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
        limitInfoDao.insertLimitInfo(limitInfoEntity);
        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            limitStoreRelationshipDao.batchInsertStoreRelationship(storeInfoList);
        }
    }

    public void updateLimitationInfo(LimitInfoEntity limitInfoEntity, List<LimitStoreRelationshipEntity> storeInfoList) {

        limitInfoDao.updateLimitInfo(limitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(limitInfoEntity.getPid());
        deleteEntity.setLimitId(limitInfoEntity.getLimitId());
        limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);

        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            limitStoreRelationshipDao.batchInsertStoreRelationship(storeInfoList);
        }
    }

    public void deleteLimitInfo(LimitInfoEntity limitInfoEntity) {
        limitInfoDao.deleteLimitInfo(limitInfoEntity);
    }

    public void deleteStoreInfoList(Long pid, Long limitId) {
        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(pid);
        deleteEntity.setLimitId(limitId);
        limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);
    }

    public void deleteGoodsLimitInfo(Long pid, Long limitId, List<Long> goodsIdList) {
        DeleteGoodsParam param = new DeleteGoodsParam();
        param.setPid(pid);
        param.setLimitId(limitId);
        param.setGoodsIdList(goodsIdList);
        if (CollectionUtils.isEmpty(goodsIdList)) {
            goodsLimitInfoDao.deleteGoodsLimitByLimitId(param);
        } else {
            goodsLimitInfoDao.deleteGoodsLimitByGoodsId(param);
        }

    }

    public void deleteSkuLimitInfo(Long pid, Long limitId, List<Long> goodsIdList) {
        deleteGoodsLimitInfo(pid, limitId, goodsIdList);
        DeleteGoodsParam param = new DeleteGoodsParam();
        param.setPid(pid);
        param.setLimitId(limitId);
        param.setGoodsIdList(goodsIdList);
        if (CollectionUtils.isEmpty(goodsIdList)) {
            skuLimitInfoDao.deleteSkuLimitByLimitId(param);
        } else {
            skuLimitInfoDao.deleteSkuLimitByGoodsId(param);
        }
    }

    public void saveGoodsLimitInfo(LimitInfoEntity limitInfoEntity, List<GoodsLimitInfoEntity> goodsLimitInfoEntity, List<SkuLimitInfoEntity> skuLimitInfoList) {
        limitInfoDao.insertLimitInfo(limitInfoEntity);
        goodsLimitInfoDao.batchInsertGoodsLimit(goodsLimitInfoEntity);
        skuLimitInfoDao.batchInsertSkuLimit(skuLimitInfoList);
    }

    public void addGoodsLimitInfoEntity(List<GoodsLimitInfoEntity> goodsLimitInfoEntity) {
        goodsLimitInfoDao.batchInsertGoodsLimit(goodsLimitInfoEntity);
    }

    public void addSkuLimitInfoList(List<SkuLimitInfoEntity> skuLimitInfoList, List<GoodsLimitInfoEntity> goodsLimitInfoEntity) {
        goodsLimitInfoDao.batchInsertGoodsLimit(goodsLimitInfoEntity);
        skuLimitInfoDao.batchInsertSkuLimit(skuLimitInfoList);
    }

    public void updateGoodsLimitInfoEntity(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList) {
        for (GoodsLimitInfoEntity goodsLimitInfoEntity : goodsLimitInfoEntityList) {
            Integer update = 0;
            //迁移过来的数据只有一条，更新为0则插入
            update = goodsLimitInfoDao.updateGoodsLimit(goodsLimitInfoEntity);
            if (update == 0) {
                List<GoodsLimitInfoEntity> goodsList = new ArrayList<>();
                goodsList.add(goodsLimitInfoEntity);
                goodsLimitInfoDao.batchInsertGoodsLimit(goodsList);
            }
        }
    }

    public void deleteDiscountLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.deleteLimitInfo(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());

        limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);

        deleteSkuLimitInfo(oldLimitInfoEntity.getPid(), oldLimitInfoEntity.getLimitId(), null);
    }

    public void deletePrivilegePriceLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.deleteLimitInfo(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());
        limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);

        deleteSkuLimitInfo(oldLimitInfoEntity.getPid(), oldLimitInfoEntity.getLimitId(), null);
    }

    public void deleteNynjLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.deleteLimitInfo(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());
        limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);
    }

    public void deleteCombinationLimitInfo(LimitInfoEntity oldLimitInfoEntity) {
        limitInfoDao.deleteLimitInfo(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());
        limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);
    }

    public void deletePointGoodsLimitInfo(LimitInfoEntity entity, List<Long> pointGoodsIdList) {
        limitInfoDao.deleteLimitInfo(entity);

        deleteSkuLimitInfo(entity.getPid(), entity.getLimitId(), pointGoodsIdList);
    }

    public List<SkuLimitInfoEntity> updatePrivilegePriceGoodsLimitInfo(List<GoodsLimitInfoEntity> oldGoodsLimitInfoEntityList, List<SkuLimitInfoEntity> skuLimitInfoList) {

        for (GoodsLimitInfoEntity oldGoodsLimitInfoEntity : oldGoodsLimitInfoEntityList) {
            Integer update = 0;
            //迁移过来的数据只有一条，更新为0则插入
            update = goodsLimitInfoDao.updateGoodsLimit(oldGoodsLimitInfoEntity);
            if (update == 0) {
                List<GoodsLimitInfoEntity> goodsList = new ArrayList<>();
                goodsList.add(oldGoodsLimitInfoEntity);
                goodsLimitInfoDao.batchInsertGoodsLimit(goodsList);
            }
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
        List<SkuLimitInfoEntity> oldSkuLimitInfoList = skuLimitInfoDao.listSkuLimitByGoodsId(limitParam);
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
                param.setGoodsId(goodsId);
                param.setSkuIdList(skuIdList);
                skuLimitInfoDao.deleteSkuLimitOnUpdateGoodsSuccess(param);
            }
            if (CollectionUtils.isNotEmpty(newSkuLimitList)) {
                skuLimitInfoDao.batchInsertSkuLimit(newSkuLimitList);
            }
        }
        return oldSkuLimitInfoList;
    }

    public void saveUserLimitRecord(List<UserGoodsLimitEntity> goodsLimitEntityList, List<UserLimitEntity> activityLimitEntityList,
                                    List<SkuLimitInfoEntity> activityGoodsSoldEntityList) {

        Integer updateResult = 0;
        if (CollectionUtils.isNotEmpty(goodsLimitEntityList)) {
            for (UserGoodsLimitEntity goodsLimitEntity : goodsLimitEntityList) {
                try {
                    UserGoodsLimitEntity oldUserGoodsLimitEntity = userGoodsLimitDao.getUserGoodsLimit(goodsLimitEntity);
                    if (oldUserGoodsLimitEntity == null) {
                        updateResult = userGoodsLimitDao.insertUserGoodsLimit(goodsLimitEntity);
                    } else {
                        updateResult = userGoodsLimitDao.updateUserGoodsLimit(goodsLimitEntity);
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
                    UserLimitEntity oldUserLimitEntity = userLimitDao.getUserLimit(activityLimitEntity);
                    if (oldUserLimitEntity == null) {
                        updateResult = userLimitDao.insertUserLimit(activityLimitEntity);
                    } else {
                        updateResult = userLimitDao.updateUserLimit(activityLimitEntity);
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
                    updateResult = skuLimitInfoDao.increaseSkuSoldNum(activitySoldEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_SOLD_NUM_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_SOLD_NUM_ERROR);
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
                    updateResult = skuLimitInfoDao.deductSkuSoldNum(activitySoldEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_SOLD_NUM_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_SKU_SOLD_NUM_ERROR);
                }
            }
        }
    }

    public void deleteDiscountUserLimitInfo(DeleteDiscountUserLimitInfoRequestVo requestVo) {
        LimitInfoEntity limitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));
        if (limitInfoEntity != null) {
            userLimitDao.deleteDiscountUserLimit(requestVo);
            userGoodsLimitDao.deleteDiscountUserGoodsLimit(new LimitParam(requestVo.getPid(), limitInfoEntity.getLimitId()));
        } else {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_ACTIVITY_IS_NULL);
        }
    }

    public void reverseUpdateGoodsLimit(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList, List<SkuLimitInfoEntity> updateSkuList, List<SkuLimitInfoEntity> insertSkuList, List<SkuLimitInfoEntity> deleteSkuList) {
        for (GoodsLimitInfoEntity oldGoodsLimitInfoEntity : goodsLimitInfoEntityList) {
            goodsLimitInfoDao.updateGoodsLimit(oldGoodsLimitInfoEntity);
        }

        if (CollectionUtils.isNotEmpty(updateSkuList)) {
            for (SkuLimitInfoEntity skuLimitInfoEntity : updateSkuList) {
                skuLimitInfoDao.updateSkuLimitNum(skuLimitInfoEntity);
            }
        }

        if (CollectionUtils.isNotEmpty(deleteSkuList)) {
            skuLimitInfoDao.deleteSkuLimitOnUpdateGoodsFail(deleteSkuList);
        }

        if (CollectionUtils.isNotEmpty(insertSkuList)) {
            skuLimitInfoDao.reverseSkuLimitStatusBySkuId(insertSkuList);
        }
    }

    public void reverseSaveGoodsLimit(LimitInfoEntity entity, List<Long> goodsList) {
        //积分商城回滚limit_info表
        if (Objects.equals(LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel(), entity.getBizType())) {
            limitInfoDao.deleteLimitInfo(entity);
        }

        deleteSkuLimitInfo(entity.getPid(), entity.getLimitId(), goodsList);
    }

    public void reverseDeleteGoodsLimit(Long pid, Integer bizType, Map<Long, List<Long>> limitIdGoodsIdMap) {
        Iterator<Map.Entry<Long, List<Long>>> iterator = limitIdGoodsIdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<Long>> entry = iterator.next();
            Long limitId = entry.getKey();
            List<Long> goodsIdList = entry.getValue();
            if (Objects.equals(LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel(), bizType)) {
                //积分商城需要回滚limit_info表
                limitInfoDao.reverseLimitInfoStatus(limitId);
            }
            //回滚商品和sku记录
            DeleteGoodsParam param = new DeleteGoodsParam();
            param.setPid(pid);
            param.setLimitId(limitId);
            param.setGoodsIdList(goodsIdList);
            goodsLimitInfoDao.reverseGoodsLimit(param);
            skuLimitInfoDao.reverseSkuLimitStatusByGoodsId(param);
        }
    }

    public void reverseDeleteLimitation(Long pid, Long limitId, HashSet<Long> goodsIdSet, List<SkuLimitInfoEntity> skuLimitInfoEntityList) {
        limitInfoDao.reverseLimitInfoStatus(limitId);

        DeleteGoodsParam param = new DeleteGoodsParam();
        param.setPid(pid);
        param.setLimitId(limitId);
        param.setGoodsIdList(new ArrayList<Long>(goodsIdSet));
        goodsLimitInfoDao.reverseGoodsLimit(param);

        if (CollectionUtils.isNotEmpty(skuLimitInfoEntityList)) {
            skuLimitInfoDao.reverseSkuLimitStatusBySkuId(skuLimitInfoEntityList);
        }
    }
}
