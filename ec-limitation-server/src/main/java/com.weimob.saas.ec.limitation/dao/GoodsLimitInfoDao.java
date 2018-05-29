package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;

public interface GoodsLimitInfoDao extends BaseDao<GoodsLimitInfoEntity>{
    int deleteByPrimaryKey(Long id);

    int insertSelective(GoodsLimitInfoEntity record);

    GoodsLimitInfoEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsLimitInfoEntity record);

    int updateByPrimaryKey(GoodsLimitInfoEntity record);
}