package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.model.request.DeleteDiscountUserLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface UserLimitDao {
    /**
     * @title 新增用户活动购买记录
     * @author qi.he
     * @date 2018/10/12 0012 17:27
     * @param [activityLimitEntity]
     * @return java.lang.Integer
     */
    Integer insertUserLimit(UserLimitEntity entity);

    /**
     * @title 删除周期性限时折扣用户活动购买记录
     * @author qi.he
     * @date 2018/10/12 0012 17:56
     * @param [requestVo]
     * @return java.lang.Integer
     */
    Integer deleteDiscountUserLimit(DeleteDiscountUserLimitInfoRequestVo requestVo);

    /**
     * @title 更新用户活动购买记录
     * @author qi.he
     * @date 2018/10/12 0012 17:31
     * @param [entity]
     * @return java.lang.Integer
     */
    Integer updateUserLimit(UserLimitEntity entity);

    /**
     * @title 减少用户活动购买记录
     * @author qi.he
     * @date 2018/10/12 0012 17:58
     * @param [activityLimitEntity]
     * @return java.lang.Integer
     */
    Integer deductUserLimit(UserLimitEntity activityLimitEntity);

    /**
     * @title 查询用户活动购买记录
     * @author qi.he
     * @date 2018/10/12 0012 17:35
     * @param [activityLimitEntity]
     * @return com.weimob.saas.ec.limitation.entity.UserLimitEntity
     */
    UserLimitEntity getUserLimit(UserLimitEntity activityLimitEntity);

    /**
     * @title 查询用户活动购买记录（所有pid,storeId,limitId下的）
     * @author fei.zheng
     * @date 2018/10/30 20:27
     * @parameterExample
     * @returnExample com.weimob.saas.ec.limitation.entity.UserLimitEntity
     */
    List<UserLimitEntity> getUserLimitListByWid(UserLimitEntity activityLimitEntity);

    /**
     * @title 批量更新用户活动购买记录（所有pid,storeId,limitId下的）
     * @author fei.zheng
     * @date 2018/10/30 20:27
     * @parameterExample
     * @returnExample com.weimob.saas.ec.limitation.entity.UserLimitEntity
     */

    Integer updateUserLimitListByWid(List<UserLimitEntity> userLimitEntityList);
    /**
     * @title 批量新增用户活动购买记录（所有pid,storeId,limitId下的）
     * @author fei.zheng
     * @date 2018/10/30 20:27
     * @parameterExample
     * @returnExample com.weimob.saas.ec.limitation.entity.UserLimitEntity
     */
    Integer saveUserLimitListByWid(List<UserLimitEntity> userLimitEntityList);

    /**
     * @title 批量删除用户活动购买记录（所有pid,storeId,limitId下的）
     * @author fei.zheng
     * @date 2018/10/30 20:27
     * @parameterExample
     * @returnExample com.weimob.saas.ec.limitation.entity.UserLimitEntity
     */
    Integer deleteUserLimitListByWid(List<UserLimitEntity> userLimitEntityList);

    /**
     * @title 批量查询用户活动购买记录
     * @author qi.he
     * @date 2018/10/12 0012 17:39
     * @scene 下单校验查询
     * @param [vos]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.UserLimitEntity>
     */
    List<UserLimitEntity> listUserLimitByBizId(List<UpdateUserLimitVo> vos);

    /**
     * @title 批量查询用户活动购买记录
     * @author qi.he
     * @date 2018/10/12 0012 17:46
     * @scene 商祥、专题页、购物车、结算
     * @param [vos]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.UserLimitEntity>
     */
    List<UserLimitEntity> listUserLimitByLimitId(List<UserLimitEntity> vos);
}