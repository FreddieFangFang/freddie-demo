package com.weimob.saas.ec.limitation.common;

/**
 * @author qi.he
 * @description 删除方式枚举
 * @date 2018/11/26 11:50
 */
public enum DeleteWayEnum {
    REMOVED_MANUALLY(1, "手动删除"),
    ACTIVITY_EXPIRE(2, "活动过期删除"),
    ;

    private Integer type;

    private String name;

    DeleteWayEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
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
