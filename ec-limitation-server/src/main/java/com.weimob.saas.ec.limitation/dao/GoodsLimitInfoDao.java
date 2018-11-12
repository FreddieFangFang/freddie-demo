package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.CommonLimitParam;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitNumListVo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface GoodsLimitInfoDao {
    /**
     * @title 批量保存商品限购信息
     * @author qi.he
     * @date 2018/10/11 0011 16:14
     * @param [goodsLimitInfoEntity]
     * @return java.lang.Integer
     */
    Integer batchInsertGoodsLimit(List<GoodsLimitInfoEntity> goodsLimitInfoEntity);

    /**
     * @title 删除商品限购（活动层面删除）
     * @author qi.he
     * @date 2018/10/11 0011 16:22
     * @param [entity]
     * @return java.lang.Integer
     */
    Integer deleteGoodsLimitByLimitId(DeleteGoodsParam entity);

    /**
     * @title 批量删除商品限购（商品层面删除）
     * @author qi.he
     * @date 2018/10/11 0011 16:23
     * @param [entity]
     * @return java.lang.Integer
     */
    Integer deleteGoodsLimitByGoodsId(DeleteGoodsParam entity);

    /**
     * @title 更新商品限购信息
     * @author qi.he
     * @date 2018/10/11 0011 16:38
     * @param [goodsLimitInfoEntity]
     * @return java.lang.Integer
     */
    Integer updateGoodsLimit(GoodsLimitInfoEntity goodsLimitInfoEntity);

    /**
     * @title 回滚商品限购信息
     * @author qi.he
     * @date 2018/10/11 0011 21:47
     * @param [param]
     * @return java.lang.Integer
     */
    Integer reverseGoodsLimit(DeleteGoodsParam param);

    /**
     * @title 通过pid,limitId,goodsId批量查询商品限购信息
     * @author qi.he
     * @date 2018/10/11 0011 16:35
     * @scene C端
     * @param [queryGoodsLimitList]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity>
     */
    List<GoodsLimitInfoEntity> listGoodsLimitByGoodsId(List<GoodsLimitInfoEntity> queryGoodsLimitList);

    /**
     * @title 查询订单中商品限购信息
     * @author qi.he
     * @date 2018/10/11 0011 16:52
     * @scene C端
     * @param [updateUserLimitVoList]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity>
     */
    List<GoodsLimitInfoEntity> listOrderGoodsLimit(List<UpdateUserLimitVo> updateUserLimitVoList);

    /**
     * @title 查询商品限购数
     * @author qi.he
     * @date 2018/10/11 0011 21:37
     * @scene B端
     * @param [queryGoodslimitNumVoList]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity>
     */
    List<GoodsLimitInfoEntity> listGoodsLimitNum(List<QueryGoodsLimitNumListVo> queryGoodslimitNumVoList);

    /**
     * @title 通过pid,limitId查询某一活动下商品限购信息
     * @author qi.he
     * @date 2018/10/11 0011 21:42
     * @param [limitParam]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity>
     */
    List<GoodsLimitInfoEntity> listGoodsLimitByLimitId(LimitParam limitParam);

    List<GoodsLimitInfoEntity> listGoodsLimitByGoodsIdList(CommonLimitParam commonLimitParam);

}