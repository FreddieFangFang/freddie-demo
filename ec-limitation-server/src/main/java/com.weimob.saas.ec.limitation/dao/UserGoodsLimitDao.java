package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.DeleteDiscountUserLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface UserGoodsLimitDao extends BaseDao<UserGoodsLimitEntity>{
    /**
     * @title 新增用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:10
     * @param [goodsLimitEntity]
     * @return java.lang.Long
     */
    Long insertUserGoodsLimit(UserGoodsLimitEntity goodsLimitEntity);

    /**
     * @title 删除周期性限时折扣用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:14
     * @param [requestVo]
     * @return void
     */
    void deleteDiscountUserGoodsLimit(LimitParam requestVo);

    /**
     * @title 更新用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:18
     * @param [goodsLimitEntity]
     * @return java.lang.Long
     */
    Long updateUserGoodsLimit(UserGoodsLimitEntity goodsLimitEntity);

    /**
     * @title 减少用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:21
     * @param [goodsLimitEntity]
     * @return int
     */
    int deductUserGoodsLimit(UserGoodsLimitEntity goodsLimitEntity);

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
     * @title 查询订单中用户商品购买记录
     * @author qi.he
     * @date 2018/10/13 0013 20:55
     * @param [vos]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity>
     */
    List<UserGoodsLimitEntity> listOrderUserGoodsLimit(List<UpdateUserLimitVo> vos);
}