package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;

public interface GoodsLimitInfoDao {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsLimitInfoEntity record);

    int insertSelective(GoodsLimitInfoEntity record);

    GoodsLimitInfoEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsLimitInfoEntity record);

    int updateByPrimaryKey(GoodsLimitInfoEntity record);
}