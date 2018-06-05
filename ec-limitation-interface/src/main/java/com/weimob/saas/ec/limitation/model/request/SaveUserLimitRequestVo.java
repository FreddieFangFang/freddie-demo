package com.weimob.saas.ec.limitation.model.request;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 增加限购入参
 * @date 2018/6/5 17:37
 */
public class SaveUserLimitRequestVo implements Serializable {


    private static final long serialVersionUID = -7163021505325719208L;

    private List<UpdateUserLimitVo> updateUserLimitVoList;

    public List<UpdateUserLimitVo> getUpdateUserLimitVoList() {
        return updateUserLimitVoList;
    }

    public void setUpdateUserLimitVoList(List<UpdateUserLimitVo> updateUserLimitVoList) {
        this.updateUserLimitVoList = updateUserLimitVoList;
    }
}
