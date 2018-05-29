package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitStoreRelationshipDao;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void saveLimitationInfo(LimitInfoEntity limitInfoEntity, List<LimitStoreRelationshipEntity> storeInfoList) {
        limitInfoDao.insert(limitInfoEntity);
        limitStoreRelationshipDao.batchInsert(storeInfoList);
    }

    public void updateLimitationInfo(LimitInfoEntity limitInfoEntity, List<LimitStoreRelationshipEntity> storeInfoList) {

        limitInfoDao.update(limitInfoEntity);

        LimitStoreRelationshipEntity deleteEntity=new LimitStoreRelationshipEntity();
        deleteEntity.setPid(limitInfoEntity.getPid());
        deleteEntity.setLimitId(limitInfoEntity.getLimitId());
        limitStoreRelationshipDao.delete(deleteEntity);

        limitStoreRelationshipDao.batchInsert(storeInfoList);
    }

}
