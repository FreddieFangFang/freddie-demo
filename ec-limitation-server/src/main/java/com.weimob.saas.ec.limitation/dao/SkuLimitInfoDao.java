package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.CommonLimitParam;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;
import com.weimob.saas.ec.limitation.model.DeleteSkuParam;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitNumListVo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface SkuLimitInfoDao {
    /**
     * @title 批量新增SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 10:30
     * @param [skuLimitInfoList]
     * @return java.lang.Integer
     */
    Integer batchInsertSkuLimit(List<SkuLimitInfoEntity> skuLimitInfoList);

    /**
     * @title 删除活动下所有SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 10:34
     * @scene 删除活动
     * @param [entity]
     * @return java.lang.Integer
     */
    Integer deleteSkuLimitByLimitId(DeleteGoodsParam entity);

    /**
     * @title 删除商品下所有SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 10:43
     * @scene 删除商品
     * @param [entity]
     * @return java.lang.Integer
     */
    Integer deleteSkuLimitByGoodsId(DeleteGoodsParam entity);

    /**
     * @title 删除某一商品下的某些SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 11:14
     * @scene 更新商品成功
     * @param [param]
     * @return java.lang.Integer
     */
    Integer deleteSkuLimitOnUpdateGoodsSuccess(DeleteSkuParam param);

    /**
     * @title 删除某一商品下的某些SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 13:55
     * @scene 更新商品失败
     * @param [deleteSkuList]
     * @return java.lang.Integer
     */
    Integer deleteSkuLimitOnUpdateGoodsFail(List<SkuLimitInfoEntity> deleteSkuList);

    /**
     * @title 更新SKU已售数量
     * @author qi.he
     * @date 2018/10/12 0012 15:05
     * @scene 下单
     * @param [activitySoldEntity]
     * @return java.lang.Integer
     */
    Integer increaseSkuSoldNum(SkuLimitInfoEntity activitySoldEntity);

    /**
     * @title 更新SKU已售数量
     * @author qi.he
     * @date 2018/10/12 0012 15:06
     * @scene 取消下单
     * @param [activitySoldEntity]
     * @return java.lang.Integer
     */
    Integer deductSkuSoldNum(SkuLimitInfoEntity activitySoldEntity);

    /**
     * @title 更新SKU可售数量
     * @author qi.he
     * @date 2018/10/12 0012 15:11
     * @param [skuLimitInfoEntity]
     * @return java.lang.Integer
     */
    Integer updateSkuLimitNum(SkuLimitInfoEntity skuLimitInfoEntity);

    /**
     * @title 翻滚SKU状态至有效-skuId级别
     * @author qi.he
     * @date 2018/10/12 0012 15:17
     * @scene 更新商品失败、删除活动失败
     * @param [insertSkuList]
     * @return java.lang.Integer
     */
    Integer reverseSkuLimitStatusBySkuId(List<SkuLimitInfoEntity> insertSkuList);

    /**
     * @title 翻滚SKU状态至有效-goodsId级别
     * @author qi.he
     * @date 2018/10/12 0012 15:25
     * @scene 删除商品失败
     * @param [param]
     * @return java.lang.Integer
     */
    Integer reverseSkuLimitStatusByGoodsId(DeleteGoodsParam param);

    /**
     * @title 批量查询SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 15:49
     * @scene C端
     * @param [querySkuLimitList]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity>
     */
    List<SkuLimitInfoEntity> listSkuLimit(List<SkuLimitInfoEntity> querySkuLimitList);

    List<SkuLimitInfoEntity> listSkuLimitBySkuList(CommonLimitParam commonLimitParam);

    /**
     * @title 查询订单中SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 15:54
     * @param [vos]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity>
     */
    List<SkuLimitInfoEntity> listOrderSkuLimit(List<UpdateUserLimitVo> vos);

    /**
     * @title 查询SKU可售数量及已售数量
     * @author qi.he
     * @date 2018/10/12 0012 16:37
     * @scene B端
     * @param [queryGoodslimitNumVoList]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity>
     */
    List<SkuLimitInfoEntity> listSkuLimitNum(List<QueryGoodsLimitNumListVo> queryGoodslimitNumVoList);

    /**
     * @title 查询活动下SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 16:42
     * @scene 删除活动
     * @param [limitParam]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity>
     */
    List<SkuLimitInfoEntity> listSkuLimitByLimitId(LimitParam limitParam);

    /**
     * @title 查询商品下SKU限购信息
     * @author qi.he
     * @date 2018/10/12 0012 16:43
     * @scene 删除商品
     * @param [limitParam]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity>
     */
    List<SkuLimitInfoEntity> listSkuLimitByGoodsId(DeleteGoodsParam limitParam);

    /**
     * @title 批量查询活动下SKU限购信息
     * @author qi.he
     * @date 2018/10/16 0016 15:44
     * @param [limitParams]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity>
     */
    List<SkuLimitInfoEntity> listSkuLimitByLimitIdList(List<LimitParam> limitParams);
}