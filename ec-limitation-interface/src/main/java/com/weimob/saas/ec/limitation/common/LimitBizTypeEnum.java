package com.weimob.saas.ec.limitation.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lujialin
 * @description 限购类型枚举
 * @date 2018/6/7 15:21
 */
public enum LimitBizTypeEnum {

    BIZ_TYPE_POINT(30, "积分商城");

    private Integer level;

    private String name;

    private static Map<Integer, LimitBizTypeEnum> BizTypeEnumMap;

    LimitBizTypeEnum(Integer level, String name) {

        this.level = level;
        this.name = name;
    }

    public static LimitBizTypeEnum getLimitLevelEnumByLevel(Integer level) {

        if (null == BizTypeEnumMap) {
            BizTypeEnumMap = new HashMap<>(values().length);
            for (LimitBizTypeEnum enumItem : values()) {
                BizTypeEnumMap.put(enumItem.getLevel(), enumItem);
            }
        }

        if (null == level) {
            return null;
        } else {
            return BizTypeEnumMap.get(level);
        }
    }

    public Integer getLevel() {

        return level;
    }

    public void setLevel(Integer level) {

        this.level = level;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
