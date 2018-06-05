package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;

import java.util.List;

public interface GoodsLimitInfoDao extends BaseDao<GoodsLimitInfoEntity>{
    int deleteByPrimaryKey(Long id);

    int insertSelective(GoodsLimitInfoEntity record);

    GoodsLimitInfoEntity selectByPrimaryKey(Long id);

    void deleteGoodsLimit(DeleteGoodsParam entity);

    void deleteLimit(DeleteGoodsParam entity);

    List<GoodsLimitInfoEntity> queryGoodsLimitInfoList(List<GoodsLimitInfoEntity> queryGoodsLimitList);

    void updateGoodsLimitInfoEntity(GoodsLimitInfoEntity goodsLimitInfoEntity);
}