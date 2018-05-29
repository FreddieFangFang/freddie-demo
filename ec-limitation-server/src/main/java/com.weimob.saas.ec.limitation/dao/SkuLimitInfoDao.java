package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;

public interface SkuLimitInfoDao {
    int deleteByPrimaryKey(Long id);

    int insert(SkuLimitInfoEntity record);

    int insertSelective(SkuLimitInfoEntity record);

    SkuLimitInfoEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SkuLimitInfoEntity record);

    int updateByPrimaryKey(SkuLimitInfoEntity record);
}