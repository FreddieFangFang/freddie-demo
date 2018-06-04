package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;

public interface GoodsLimitInfoDao extends BaseDao<GoodsLimitInfoEntity>{
    int deleteByPrimaryKey(Long id);

    int insertSelective(GoodsLimitInfoEntity record);

    GoodsLimitInfoEntity selectByPrimaryKey(Long id);

    void deleteGoodsLimit(DeleteGoodsParam entity);

    void deleteLimit(DeleteGoodsParam entity);
}