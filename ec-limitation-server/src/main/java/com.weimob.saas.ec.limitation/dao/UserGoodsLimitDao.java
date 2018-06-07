package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface UserGoodsLimitDao extends BaseDao<UserGoodsLimitEntity>{
    int deleteByPrimaryKey(Long id);

    UserGoodsLimitEntity selectByPrimaryKey(Long id);

    List<UserGoodsLimitEntity> queryUserGoodsLimitList(List<UserGoodsLimitEntity> queryUserGoodsLimitList);

    List<UserGoodsLimitEntity> queryUserOrderGoodsLimitList(List<UpdateUserLimitVo> vos);

    UserGoodsLimitEntity getUserGoodsLimitEntity(UserGoodsLimitEntity goodsLimitEntity);
}