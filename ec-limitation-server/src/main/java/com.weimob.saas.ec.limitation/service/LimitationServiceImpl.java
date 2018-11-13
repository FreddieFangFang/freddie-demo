package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.dao.GoodsLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitStoreRelationshipDao;
import com.weimob.saas.ec.limitation.dao.SkuLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.UserGoodsLimitDao;
import com.weimob.saas.ec.limitation.dao.UserLimitDao;
import com.weimob.saas.ec.limitation.entity.*;
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
        Integer updateResult = 0;
        try {
            updateResult = limitInfoDao.deleteLimitInfo(oldLimitInfoEntity);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_DELETE_LIMIT_INFO_ERROR, e);
        }
        if (updateResult == 0) {
            throw new LimitationBizException(LimitationErrorCode.SQL_DELETE_LIMIT_INFO_ERROR);
        }

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());
        try {
            limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_DELETE_STORE_RELATIONSHIP_ERROR, e);
        }

        DeleteGoodsParam param = new DeleteGoodsParam();
        param.setPid(oldLimitInfoEntity.getPid());
        param.setLimitId(oldLimitInfoEntity.getLimitId());
        try {
            updateResult = skuLimitInfoDao.deleteSkuLimitByLimitId(param);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_DELETE_SKU_LIMIT_NUM_ERROR, e);
        }
        if (updateResult == 0) {
            throw new LimitationBizException(LimitationErrorCode.SQL_DELETE_SKU_LIMIT_NUM_ERROR);
        }
    }

    public void deletePointGoodsLimitInfo(LimitInfoEntity entity, List<Long> pointGoodsIdList) {
        limitInfoDao.deleteLimitInfo(entity);

        deleteSkuLimitInfo(entity.getPid(), entity.getLimitId(), pointGoodsIdList);
    }
    public void deleteCommunityGrouponLimitInfo(LimitInfoEntity oldLimitInfoEntity, List<Long> goodsIdList) {
        limitInfoDao.deleteLimitInfo(oldLimitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity = new LimitStoreRelationshipEntity();
        deleteEntity.setPid(oldLimitInfoEntity.getPid());
        deleteEntity.setLimitId(oldLimitInfoEntity.getLimitId());
        limitStoreRelationshipDao.deleteStoreRelationship(deleteEntity);

        deleteSkuLimitInfo(oldLimitInfoEntity.getPid(), oldLimitInfoEntity.getLimitId(), goodsIdList);
    }
    public List<SkuLimitInfoEntity> updatePrivilegePriceGoodsLimitInfo(List<GoodsLimitInfoEntity> newGoodsLimitInfoEntityList, List<SkuLimitInfoEntity> skuLimitInfoList) {

        Long pid = null;
        Long limitId = null;
        if (CollectionUtils.isNotEmpty(newGoodsLimitInfoEntityList)) {
            for (GoodsLimitInfoEntity goodsLimitInfoEntity : newGoodsLimitInfoEntityList) {
                Integer update = 0;
                //迁移过来的数据只有一条，更新为0则插入
                update = goodsLimitInfoDao.updateGoodsLimit(goodsLimitInfoEntity);
                if (update == 0) {
                    List<GoodsLimitInfoEntity> goodsList = new ArrayList<>();
                    goodsList.add(goodsLimitInfoEntity);
                    goodsLimitInfoDao.batchInsertGoodsLimit(goodsList);
                }
            }
            pid = newGoodsLimitInfoEntityList.get(0).getPid();
            limitId = newGoodsLimitInfoEntityList.get(0).getLimitId();
        } else {
            pid = skuLimitInfoList.get(0).getPid();
            limitId = skuLimitInfoList.get(0).getLimitId();
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
        // 新增/更新用户商品购买记录
        updateUserGoodsLimit(goodsLimitEntityList);

        // 新增/更新用户活动购买记录
        updateUserLimit(activityLimitEntityList);

        // 更新SKU表已售数量
        updateSkuSoldNum(activityGoodsSoldEntityList);
    }

    /**
     * 修改用户购买记录
     * @param goodsLimitEntityList
     * @param activityLimitEntityList
     * @param activityGoodsSoldEntityList
     * @scenes 取消订单/维权
     * @author Pengqin ZHOU
     * @date 2018/6/8
     */
    public void updateUserLimitRecord(List<UserGoodsLimitEntity> goodsLimitEntityList, List<UserLimitEntity> activityLimitEntityList,
                                      List<SkuLimitInfoEntity> activityGoodsSoldEntityList) {

        Integer updateResult;
        if (!CollectionUtils.isEmpty(goodsLimitEntityList)) {
            Collections.sort(goodsLimitEntityList);
            for (UserGoodsLimitEntity goodsLimitEntity : goodsLimitEntityList) {
                try {
                    updateResult = userGoodsLimitDao.deductUserGoodsLimit(goodsLimitEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_DEDUCT_BUY_NUM_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.ACTIVITY_EXPIRED);
                }
            }
        }

        if (!CollectionUtils.isEmpty(activityLimitEntityList)) {
            Collections.sort(activityLimitEntityList);
            for (UserLimitEntity activityLimitEntity : activityLimitEntityList) {
                try {
                    updateResult = userLimitDao.deductUserLimit(activityLimitEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_LIMIT_DEDUCT_BUY_NUM_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.ACTIVITY_EXPIRED);
                }
            }
        }

        if (!CollectionUtils.isEmpty(activityGoodsSoldEntityList)) {
            Collections.sort(activityGoodsSoldEntityList);
            for (SkuLimitInfoEntity activitySoldEntity : activityGoodsSoldEntityList) {
                try {
                    updateResult = skuLimitInfoDao.deductSkuSoldNum(activitySoldEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_DEDUCT_SKU_SOLD_NUM_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.ACTIVITY_EXPIRED);
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
            if (!Objects.equals(LimitBizTypeEnum.BIZ_TYPE_COMMUNITY_GROUPON.getLevel(), bizType)) {
                goodsLimitInfoDao.reverseGoodsLimit(param);
            }
            skuLimitInfoDao.reverseSkuLimitStatusByGoodsId(param);
        }
    }

    public void reverseDeleteLimitation(Long pid, Long limitId, HashSet<Long> goodsIdSet,
                                        List<SkuLimitInfoEntity> skuLimitInfoEntityList,
                                        List<LimitOrderChangeLogEntity> logList) {
        limitInfoDao.reverseLimitInfoStatus(limitId);

        if (!Objects.equals(logList.get(0).getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())
                ||!Objects.equals(logList.get(0).getBizType(), ActivityTypeEnum.COMMUNITY_GROUPON.getType())) {
            DeleteGoodsParam param = new DeleteGoodsParam();
            param.setPid(pid);
            param.setLimitId(limitId);
            param.setGoodsIdList(new ArrayList<>(goodsIdSet));
            goodsLimitInfoDao.reverseGoodsLimit(param);
        }

        if (CollectionUtils.isNotEmpty(skuLimitInfoEntityList)) {
            skuLimitInfoDao.reverseSkuLimitStatusBySkuId(skuLimitInfoEntityList);
        }
    }

    private void updateSkuSoldNum(List<SkuLimitInfoEntity> activityGoodsSoldEntityList) {
        Integer updateResult;
        if (CollectionUtils.isNotEmpty(activityGoodsSoldEntityList)) {
            Collections.sort(activityGoodsSoldEntityList);
            for (SkuLimitInfoEntity activitySoldEntity : activityGoodsSoldEntityList) {
                try {
                    updateResult = skuLimitInfoDao.increaseSkuSoldNum(activitySoldEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_INCREASE_SKU_SOLD_NUM_ERROR, e);
                }
                if (updateResult == 0) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_INCREASE_SKU_SOLD_NUM_ERROR);
                }
            }
        }
    }

    private void updateUserLimit(List<UserLimitEntity> activityLimitEntityList) {
        Integer updateResult;
        if (CollectionUtils.isNotEmpty(activityLimitEntityList)) {
            Collections.sort(activityLimitEntityList);
            for (UserLimitEntity activityLimitEntity : activityLimitEntityList) {
                UserLimitEntity oldUserLimitEntity;
                try {
                    oldUserLimitEntity = userLimitDao.getUserLimit(activityLimitEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_USER_LIMIT_ERROR, e);
                }
                if (oldUserLimitEntity == null) {
                    try {
                        updateResult = userLimitDao.insertUserLimit(activityLimitEntity);
                    } catch (Exception e) {
                        throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_USER_LIMIT_ERROR, e);
                    }
                    if (updateResult == 0) {
                        throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_USER_LIMIT_ERROR);
                    }
                } else {
                    try {
                        updateResult = userLimitDao.updateUserLimit(activityLimitEntity);
                    } catch (Exception e) {
                        throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_LIMIT_INCREASE_BUY_NUM_ERROR, e);
                    }
                    if (updateResult == 0) {
                        throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_LIMIT_INCREASE_BUY_NUM_ERROR);
                    }
                }
            }
        }
    }

    private void updateUserGoodsLimit(List<UserGoodsLimitEntity> goodsLimitEntityList) {
        Integer updateResult;
        if (CollectionUtils.isNotEmpty(goodsLimitEntityList)) {
            Collections.sort(goodsLimitEntityList);
            for (UserGoodsLimitEntity goodsLimitEntity : goodsLimitEntityList) {
                UserGoodsLimitEntity oldUserGoodsLimitEntity;
                try {
                    oldUserGoodsLimitEntity = userGoodsLimitDao.getUserGoodsLimit(goodsLimitEntity);
                } catch (Exception e) {
                    throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_USER_GOODS_LIMIT_ERROR, e);
                }
                if (oldUserGoodsLimitEntity == null) {
                    try {
                        updateResult = userGoodsLimitDao.insertUserGoodsLimit(goodsLimitEntity);
                    } catch (Exception e) {
                        throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_USER_GOODS_LIMIT_ERROR, e);
                    }
                    if (updateResult == 0) {
                        throw new LimitationBizException(LimitationErrorCode.SQL_SAVE_USER_GOODS_LIMIT_ERROR);
                    }
                } else {
                    try {
                        updateResult = userGoodsLimitDao.updateUserGoodsLimit(goodsLimitEntity);
                    } catch (Exception e) {
                        throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_INCREASE_BUY_NUM_ERROR, e);
                    }
                    if (updateResult == 0) {
                        throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_USER_GOODS_LIMIT_INCREASE_BUY_NUM_ERROR);
                    }
                }
            }
        }
    }
}
