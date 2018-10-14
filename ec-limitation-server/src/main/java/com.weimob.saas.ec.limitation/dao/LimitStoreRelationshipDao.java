package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;

import java.util.List;

public interface LimitStoreRelationshipDao {
    /**
     * @title 批量插入门店关系
     * @author qi.he
     * @date 2018/10/12 0012 17:14
     * @param [storeInfoList]
     * @return java.lang.Integer
     */
    Integer batchInsertStoreRelationship(List<LimitStoreRelationshipEntity> storeInfoList);

    /**
     * @title 删除门店关联关系
     * @author qi.he
     * @date 2018/10/12 0012 17:19
     * @param [deleteEntity]
     * @return java.lang.Integer
     */
    Integer deleteStoreRelationship(LimitStoreRelationshipEntity deleteEntity);
}