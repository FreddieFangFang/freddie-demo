package com.weimob.saas.ec.limitation.model.response;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;

/**
 * @author lujialin
 * @description 回滚限购记录出参
 * @date 2018/6/5 17:49
 */
public class ReverseUserLimitResponseVo implements Serializable{

    private static final long serialVersionUID = -3442198604963570478L;

    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
