package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;

public interface LimitInfoDao extends BaseDao<LimitInfoEntity> {
    int deleteByPrimaryKey(Long limitId);

    int insertSelective(LimitInfoEntity record);

    LimitInfoEntity selectByPrimaryKey(Long limitId);

    int updateByPrimaryKeySelective(LimitInfoEntity record);

    int updateByPrimaryKey(LimitInfoEntity record);
}