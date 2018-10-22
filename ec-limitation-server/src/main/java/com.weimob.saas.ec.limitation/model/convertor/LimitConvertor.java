package com.weimob.saas.ec.limitation.model.convertor;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.model.UserLimitBaseBo;

import java.util.Objects;

/**
 * @author lujialin
 * @description 限购信息转化为数据库字段信息
 * @date 2018/6/7 10:17
 */
public class LimitConvertor {
    public static UserGoodsLimitEntity convertGoodsLimit(UserLimitBaseBo baseBo, long goodsId, int goodsNum, LimitInfoEntity limitInfoEntity) {
        UserGoodsLimitEntity entity = new UserGoodsLimitEntity();
        entity.setPid(baseBo.getPid());
        entity.setStoreId(baseBo.getStoreId());
        entity.setWid(baseBo.getWid());
        entity.setLimitId(limitInfoEntity.getLimitId());
        entity.setGoodsId(goodsId);
        entity.setBuyNum(goodsNum);
        return entity;
    }

    public static UserLimitEntity convertActivityLimit(UserLimitBaseBo baseBo, long activityId, int goodsNum, LimitInfoEntity limitInfoEntity) {
        UserLimitEntity entity = new UserLimitEntity();
        entity.setPid(baseBo.getPid());
        entity.setStoreId(baseBo.getStoreId());
        entity.setWid(baseBo.getWid());
        entity.setLimitId(limitInfoEntity.getLimitId());
        entity.setBizType(baseBo.getBizType());
        entity.setBizId(baseBo.getBizId());
        entity.setBuyNum(goodsNum);
        return entity;
    }

    public static SkuLimitInfoEntity convertActivitySoldEntity(UserLimitBaseBo baseBo, long skuId, int goodsNum, LimitInfoEntity limitInfoEntity) {
        SkuLimitInfoEntity entity = new SkuLimitInfoEntity();
        entity.setPid(baseBo.getPid());
        entity.setStoreId(baseBo.getStoreId());
        entity.setLimitId(limitInfoEntity.getLimitId());
        // 优惠套装活动、skuId=bizId=goodsId
        entity.setSkuId(skuId);
        entity.setGoodsId(baseBo.getGoodsId());
        entity.setSoldNum(goodsNum);
        if (Objects.equals(limitInfoEntity.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
            entity.setGoodsId(skuId);
        }
        return entity;
    }
}
