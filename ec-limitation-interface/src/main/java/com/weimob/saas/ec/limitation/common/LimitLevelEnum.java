package com.weimob.saas.ec.limitation.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 限购级别
 *
 * @author Pengqin ZHOU
 * @date 2018/5/30
 */
public enum LimitLevelEnum {

    LIMIT_LEVEL_ACTIVITY(0, "活动限购"),
    LIMIT_LEVEL_GOODS(1, "商品限购"),
    LIMIT_LEVEL_SKU(2, "SKU 限购");

    private Integer level;

    private String name;

    private static Map<Integer, LimitLevelEnum> levelEnumMap;

    LimitLevelEnum(Integer level, String name) {

        this.level = level;
        this.name = name;
    }

    public static LimitLevelEnum getLimitLevelEnumByLevel(Integer level) {

        if (null == levelEnumMap) {
            levelEnumMap = new HashMap<>(values().length);
            for (LimitLevelEnum enumItem : values()) {
                levelEnumMap.put(enumItem.getLevel(), enumItem);
            }
        }

        if (null == level) {
            return null;
        } else {
            return levelEnumMap.get(level);
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
