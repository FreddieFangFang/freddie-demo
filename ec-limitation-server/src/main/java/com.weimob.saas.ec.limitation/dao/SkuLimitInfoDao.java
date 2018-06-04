package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;

public interface SkuLimitInfoDao extends BaseDao<SkuLimitInfoEntity>{
    int deleteByPrimaryKey(Long id);

    int insertSelective(SkuLimitInfoEntity record);

    SkuLimitInfoEntity selectByPrimaryKey(Long id);

    void deleteSkuLimit(DeleteGoodsParam entity);

    void deleteLimit(DeleteGoodsParam entity);
}