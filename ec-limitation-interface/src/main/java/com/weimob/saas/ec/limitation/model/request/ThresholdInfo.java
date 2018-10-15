package com.weimob.saas.ec.limitation.model.request;

import java.io.Serializable;

/**
 * @Description 活动可售数量相关信息
 * @Author qi.he
 * @Date 2018-10-15 10:48
 */
public class ThresholdInfo implements Serializable{

    private static final long serialVersionUID = 7766929289412334050L;

    /** 可售数量 */
    private Integer threshold;

    /** 特定人群，取值范围，参见：{@link com.weimob.saas.ec.limitation.common.LimitTypeEnum} */
    private Integer particularGroupType;

    public ThresholdInfo() {
    }

    public ThresholdInfo(Integer threshold, Integer particularGroupType) {
        this.threshold = threshold;
        this.particularGroupType = particularGroupType;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public Integer getParticularGroupType() {
        return particularGroupType;
    }

    public void setParticularGroupType(Integer particularGroupType) {
        this.particularGroupType = particularGroupType;
    }
}
