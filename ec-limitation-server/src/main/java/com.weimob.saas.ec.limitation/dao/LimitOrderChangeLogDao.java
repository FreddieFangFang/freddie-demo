package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;

public interface LimitOrderChangeLogDao extends BaseDao<LimitOrderChangeLogEntity>{
    int deleteByPrimaryKey(Long id);

    int insertSelective(LimitOrderChangeLogEntity record);

    LimitOrderChangeLogEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LimitOrderChangeLogEntity record);

    int updateByPrimaryKey(LimitOrderChangeLogEntity record);
}