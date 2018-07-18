package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.DeleteGoodsParam;
import com.weimob.saas.ec.limitation.model.DeleteSkuParam;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitNumListVo;
import com.weimob.saas.ec.limitation.model.request.SkuLimitInfo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;

import java.util.List;

public interface SkuLimitInfoDao extends BaseDao<SkuLimitInfoEntity>{
    void deleteSkuLimit(DeleteGoodsParam entity);

    void deleteLimit(DeleteGoodsParam entity);

    List<SkuLimitInfo> queryOrderSkuLimitInfoList(List<UpdateUserLimitVo> vos);

    List<SkuLimitInfoEntity> querySkuLimitList(List<SkuLimitInfoEntity> querySkuLimitList);

    Integer deductSkuLimit(SkuLimitInfoEntity activitySoldEntity);

    List<SkuLimitInfoEntity> queryGoodsSkuLimitList(List<QueryGoodsLimitNumListVo> queryGoodslimitNumVoList);

    List<SkuLimitInfoEntity> queryOldSkuLimitList(LimitParam limitParam);

    void updateSkuLimitNum(SkuLimitInfoEntity skuLimitInfoEntity);

    void deleteGoodsSkuLimit(DeleteSkuParam param);
}