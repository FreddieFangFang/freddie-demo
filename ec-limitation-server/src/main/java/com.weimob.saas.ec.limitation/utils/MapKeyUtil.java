package com.weimob.saas.ec.limitation.utils;

/**
 * @author lujialin
 * @description 生成map的key
 * @date 2018/6/4 15:04
 */
public class MapKeyUtil {

    public static String buildLimitIdMapKey(Long pid, Integer bizType, Long bizId) {
        return pid + "_" + bizType + "_" + bizId;
    }

    public static String buildGoodsLimitNumMap(Long pid, Long storeId, Long limitId, Long goodsId) {
        return pid + "_" + storeId + "_" + limitId + "_" + goodsId;
    }

    public static String buildUserGoodsLimitNumMap(Long pid, Long storeId, Long wid, Long limitId, Long goodsId) {
        return pid + "_" + storeId + "_" + wid + "_" + limitId + "_" + goodsId;
    }

    public static String buildPidStoreIdGoodsId(Long pid, Long storeId, Long goodsId) {
        return pid + "_" + storeId + "_" + goodsId;
    }
}
