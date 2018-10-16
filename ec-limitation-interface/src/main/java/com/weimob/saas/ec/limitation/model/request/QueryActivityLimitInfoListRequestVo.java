package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

import java.util.List;

/**
 * @Description 批量查询活动限购信息入参
 * @Author qi.he
 * @Date 2018-10-16 14:45
 */
public class QueryActivityLimitInfoListRequestVo extends BaseRequest {

    private static final long serialVersionUID = 8447338483819937635L;

    private List<Long> bizIds;

    private Integer bizType;

    public List<Long> getBizIds() {
        return bizIds;
    }

    public void setBizIds(List<Long> bizIds) {
        this.bizIds = bizIds;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }
}
