package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.model.request.DeleteDiscountUserLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface UserLimitDao extends BaseDao<UserLimitEntity> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(UserLimitEntity record);

    UserLimitEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserLimitEntity record);

    int updateByPrimaryKey(UserLimitEntity record);

    List<UserLimitEntity> queryUserLimitInfoList(List<UpdateUserLimitVo> vos);

    UserLimitEntity getUserLimitEntity(UserLimitEntity activityLimitEntity);

    List<UserLimitEntity> queryUserLimitEntityList(List<UserLimitEntity> vos);

    Integer deductUserLimit(UserLimitEntity activityLimitEntity);

    void deleteDiscountUserLimitInfo(DeleteDiscountUserLimitInfoRequestVo requestVo);
}