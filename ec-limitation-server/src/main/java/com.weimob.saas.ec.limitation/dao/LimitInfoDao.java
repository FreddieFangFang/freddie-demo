package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.model.LimitParam;

public interface LimitInfoDao extends BaseDao<LimitInfoEntity> {
    LimitInfoEntity selectByLimitParam(LimitParam limitParam);

    int deleteByPrimaryKey(Long limitId);

    LimitInfoEntity selectByPrimaryKey(Long limitId);

}