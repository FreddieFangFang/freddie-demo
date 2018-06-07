package com.weimob.saas.ec.limitation.model.convertor;

import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.model.UserLimitBaseBo;

/**
 * @author lujialin
 * @description 限购信息转化为数据库字段信息
 * @date 2018/6/7 10:17
 */
public class LimitConvertor {
    public static UserGoodsLimitEntity convertGoodsLimit(UserLimitBaseBo baseBo, long goodsId, int goodsNum) {
        UserGoodsLimitEntity entity = new UserGoodsLimitEntity();
        entity.setPid(baseBo.getPid());
        entity.setStoreId(baseBo.getStoreId());
        entity.setWid(baseBo.getWid());
        //entity.setb(baseBo.getActivityId());
        entity.setGoodsId(goodsId);
        entity.setBuyNum(goodsNum);
        return entity;
    }

    public static UserLimitEntity convertActivityLimit(UserLimitBaseBo baseBo, long activityId, int goodsNum) {
        UserLimitEntity entity = new UserLimitEntity();
        entity.setPid(baseBo.getPid());
        entity.setStoreId(baseBo.getStoreId());
        entity.setWid(baseBo.getWid());
        entity.setBizId(baseBo.getBizId());
        entity.setBuyNum(goodsNum);
        return entity;
    }

    public static SkuLimitInfoEntity convertActivitySoldEntity(UserLimitBaseBo baseBo, long skuId, int goodsNum) {
        SkuLimitInfoEntity entity = new SkuLimitInfoEntity();
        entity.setPid(baseBo.getPid());
        entity.setStoreId(baseBo.getStoreId());
        /*entity.setWid(baseBo.getWid());
        entity.(baseBo.getActivityId());*/
        entity.setSkuId(skuId);
        entity.setGoodsId(baseBo.getGoodsId());
        entity.setSoldNum(goodsNum);
        return entity;
    }
}
