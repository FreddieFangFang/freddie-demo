package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;

import java.util.List;

public interface UserGoodsLimitDao extends BaseDao<UserGoodsLimitEntity>{
    int deleteByPrimaryKey(Long id);

    int insertSelective(UserGoodsLimitEntity record);

    UserGoodsLimitEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserGoodsLimitEntity record);

    int updateByPrimaryKey(UserGoodsLimitEntity record);

    List<UserGoodsLimitEntity> queryUserGoodsLimitList(List<UserGoodsLimitEntity> queryUserGoodsLimitList);

}