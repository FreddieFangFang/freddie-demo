package com.weimob.saas.ec.limitation.model.response;

import java.util.Date;

/**
 * @author lujialin
 * @description 限购迁移出参
 * @date 2018/6/29 17:27
 */
public class LimitationTransferResponseVo {

    private String startTime;

    private String endTime;

    private Boolean status;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
