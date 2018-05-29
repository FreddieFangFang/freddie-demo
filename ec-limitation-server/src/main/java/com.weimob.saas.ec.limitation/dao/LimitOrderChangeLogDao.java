package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;

public interface LimitOrderChangeLogDao {
    int deleteByPrimaryKey(Long id);

    int insert(LimitOrderChangeLogEntity record);

    int insertSelective(LimitOrderChangeLogEntity record);

    LimitOrderChangeLogEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LimitOrderChangeLogEntity record);

    int updateByPrimaryKey(LimitOrderChangeLogEntity record);
}