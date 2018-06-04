package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 批量移除限购商品
 * @date 2018/5/31 10:00
 */
public class BatchDeleteGoodsLimitRequestVo implements Serializable {

    private static final long serialVersionUID = 3726126792123541928L;

    private List<BatchDeleteGoodsLimitVo> deleteGoodsLimitVoList;

    public List<BatchDeleteGoodsLimitVo> getDeleteGoodsLimitVoList() {
        return deleteGoodsLimitVoList;
    }

    public void setDeleteGoodsLimitVoList(List<BatchDeleteGoodsLimitVo> deleteGoodsLimitVoList) {
        this.deleteGoodsLimitVoList = deleteGoodsLimitVoList;
    }
}
