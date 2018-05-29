package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserLimitEntity;

public interface UserLimitDao extends BaseDao<UserLimitEntity>{
    int deleteByPrimaryKey(Long id);

    int insertSelective(UserLimitEntity record);

    UserLimitEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserLimitEntity record);

    int updateByPrimaryKey(UserLimitEntity record);
}