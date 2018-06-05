package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;

public interface SkuLimitInfoDao extends BaseDao<SkuLimitInfoEntity>{
    void deleteSkuLimit(DeleteGoodsParam entity);

    void deleteLimit(DeleteGoodsParam entity);
}