package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.DeleteDiscountUserLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface UserGoodsLimitDao {
    /**
     * @title 新增用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:10
     * @param [goodsLimitEntity]
     * @return java.lang.Integer
     */
    Integer insertUserGoodsLimit(UserGoodsLimitEntity goodsLimitEntity);

    /**
     * @title 删除周期性限时折扣用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:14
     * @param [requestVo]
     * @return java.lang.Integer
     */
    Integer deleteDiscountUserGoodsLimit(LimitParam requestVo);

    /**
     * @title 更新用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:18
     * @param [goodsLimitEntity]
     * @return java.lang.Integer
     */
    Integer updateUserGoodsLimit(UserGoodsLimitEntity goodsLimitEntity);

    /**
     * @title 减少用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:21
     * @param [goodsLimitEntity]
     * @return java.lang.Integer
     */
    Integer deductUserGoodsLimit(UserGoodsLimitEntity goodsLimitEntity);

    /**
     * @title 查询用户活动购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:23
     * @param [goodsLimitEntity]
     * @return com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity
     */
    UserGoodsLimitEntity getUserGoodsLimit(UserGoodsLimitEntity goodsLimitEntity);

    /**
     * @title 批量查询用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:40
     * @param [queryUserGoodsLimitList]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity>
     */
    List<UserGoodsLimitEntity> listUserGoodsLimit(List<UserGoodsLimitEntity> queryUserGoodsLimitList);

    /**
     * @title 查询用户活动购买记录（所有pid,storeId,limitId下的）
     * @author fei.zheng
     * @date 2018/10/31 10:06
     * @parameterExample
     * @returnExample
     */
    List<UserGoodsLimitEntity> listUserGoodsLimitListByWid(UserGoodsLimitEntity queryUserGoodsLimit);

    /**
     * @title 批量修改用户活动购买记录（所有pid,storeId,limitId下的）
     * @author fei.zheng
     * @date 2018/10/31 10:06
     * @parameterExample
     * @returnExample
     */
    Integer updateUserGoodsLimitListByWid(List<UserGoodsLimitEntity> userGoodsLimitEntityList);

    /**
     * @title 批量新增查询用户活动购买记录（所有pid,storeId,limitId下的）
     * @author fei.zheng
     * @date 2018/10/31 10:06
     * @parameterExample
     * @returnExample
     */
    Integer saveUserGoodsLimitListByWid(List<UserGoodsLimitEntity> userGoodsLimitEntityList);

    /**
     * @title 查询订单中用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:55
     * @param [vos]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity>
     */
    List<UserGoodsLimitEntity> listOrderUserGoodsLimit(List<UpdateUserLimitVo> vos);
}