package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;

public interface UserGoodsLimitDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserGoodsLimitEntity record);

    int insertSelective(UserGoodsLimitEntity record);

    UserGoodsLimitEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserGoodsLimitEntity record);

    int updateByPrimaryKey(UserGoodsLimitEntity record);
}