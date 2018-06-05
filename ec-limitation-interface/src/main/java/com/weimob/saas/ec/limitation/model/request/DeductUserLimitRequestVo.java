package com.weimob.saas.ec.limitation.model.request;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 减少限购记录入参
 * @date 2018/6/5 17:46
 */
public class DeductUserLimitRequestVo implements Serializable {
    private static final long serialVersionUID = 9118797154533074931L;

    private List<UpdateUserLimitVo> updateUserLimitVoList;

    public List<UpdateUserLimitVo> getUpdateUserLimitVoList() {
        return updateUserLimitVoList;
    }

    public void setUpdateUserLimitVoList(List<UpdateUserLimitVo> updateUserLimitVoList) {
        this.updateUserLimitVoList = updateUserLimitVoList;
    }
}
