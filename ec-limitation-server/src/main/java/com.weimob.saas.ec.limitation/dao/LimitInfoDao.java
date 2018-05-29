package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;

public interface LimitInfoDao {
    int deleteByPrimaryKey(Long limitId);

    int insert(LimitInfoEntity record);

    int insertSelective(LimitInfoEntity record);

    LimitInfoEntity selectByPrimaryKey(Long limitId);

    int updateByPrimaryKeySelective(LimitInfoEntity record);

    int updateByPrimaryKey(LimitInfoEntity record);
}