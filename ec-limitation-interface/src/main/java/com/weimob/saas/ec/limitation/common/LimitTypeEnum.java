package com.weimob.saas.ec.limitation.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 限购维度（个人/所有人）
 *
 * @author Pengqin ZHOU
 * @date 2018/5/30
 */
public enum LimitTypeEnum {

    LIMIT_TYPE_ONE(0, "每个人"),
    LIMIT_TYPE_ALL(1, "所有人");

    private Integer type;

    private String name;

    private static Map<Integer, LimitTypeEnum> typeEnumMap;

    LimitTypeEnum(Integer type, String name) {

        this.type = type;
        this.name = name;
    }

    public static LimitTypeEnum getLimitTypeEnumByType(Integer type) {

        if (null == typeEnumMap) {
            typeEnumMap = new HashMap<>(values().length);
            for (LimitTypeEnum enumItem : values()) {
                typeEnumMap.put(enumItem.getType(), enumItem);
            }
        }

        if (null == type) {
            return null;
        } else {
            return typeEnumMap.get(type);
        }
    }

    public Integer getType() {

        return type;
    }

    public void setType(Integer type) {

        this.type = type;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
