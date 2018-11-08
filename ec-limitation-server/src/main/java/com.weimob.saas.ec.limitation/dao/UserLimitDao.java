package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.model.CommonLimitParam;
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

    List<UserLimitEntity> listUserLimitByLimitIdList(CommonLimitParam commonLimitParam);
}