package com.weimob.saas.ec.limitation.model.response;

import com.weimob.saas.ec.common.request.BaseRequest;

/**
 * @author lujialin
 * @description 查询活动限购数量
 * @date 2018/6/8 14:44
 */
public class QueryActivityLimitInfoResponseVo extends BaseRequest {

    private Long bizId;

    private Integer bizType;

    /** 限购数量 */
    private Integer activityLimitNum;

    /** 可售数量 */
    private Integer threshold;

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Integer getActivityLimitNum() {
        return activityLimitNum;
    }

    public void setActivityLimitNum(Integer activityLimitNum) {
        this.activityLimitNum = activityLimitNum;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
