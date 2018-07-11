package com.weimob.saas.ec.limitation.common;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lujialin
 * @description 限购类型枚举
 * @date 2018/6/7 15:21
 */
public enum LimitBizTypeEnum {

    BIZ_TYPE_POINT(30, "积分商城"),
    BIZ_TYPE_DISCOUNT(ActivityTypeEnum.DISCOUNT.getType(), "限时折扣"),
    BIZ_TYPE_PRIVILEGE_PRICE(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), "特权价"),
    BIZ_TYPE_NYNJ(ActivityTypeEnum.NYNJ.getType(), "N元N件"),
    BIZ_TYPE_COMBINATION_BUY(ActivityTypeEnum.COMBINATION_BUY.getType(), "优惠套装"),
    BIZ_TYPE_REDEMPTION(ActivityTypeEnum.REDEMPTION.getType(), "加价购");

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
