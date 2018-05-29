package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;

public interface SkuLimitInfoDao extends BaseDao<SkuLimitInfoEntity>{
    int deleteByPrimaryKey(Long id);

    int insertSelective(SkuLimitInfoEntity record);

    SkuLimitInfoEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SkuLimitInfoEntity record);

    int updateByPrimaryKey(SkuLimitInfoEntity record);
}