package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;

public interface LimitStoreRelationshipDao extends BaseDao<LimitStoreRelationshipEntity> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(LimitStoreRelationshipEntity record);

    LimitStoreRelationshipEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LimitStoreRelationshipEntity record);

    int updateByPrimaryKey(LimitStoreRelationshipEntity record);
}