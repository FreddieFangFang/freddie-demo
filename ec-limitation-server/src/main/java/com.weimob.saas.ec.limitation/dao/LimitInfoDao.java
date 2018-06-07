package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface LimitInfoDao extends BaseDao<LimitInfoEntity> {
    LimitInfoEntity selectByLimitParam(LimitParam limitParam);

    int deleteByPrimaryKey(Long limitId);

    LimitInfoEntity selectByPrimaryKey(Long limitId);

    List<LimitInfoEntity> queryLimitInfoList(List<LimitParam> limitParams);

    List<LimitInfoEntity> queryOrderLimitInfoList(List<UpdateUserLimitVo> updateUserLimitVoList);
}