package com.weimob.saas.ec.limitation.model.request;

/**
 * @author lujialin
 * @description 限购迁移入参
 * @date 2018/6/29 17:25
 */
public class LimitationTransferRequestVo {

    private String limitationAuth;

    public String getLimitationAuth() {
        return limitationAuth;
    }

    public void setLimitationAuth(String limitationAuth) {
        this.limitationAuth = limitationAuth;
    }
}
