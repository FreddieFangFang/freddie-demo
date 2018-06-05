package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.dao.GoodsLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitStoreRelationshipDao;
import com.weimob.saas.ec.limitation.dao.SkuLimitInfoDao;
import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
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
}
