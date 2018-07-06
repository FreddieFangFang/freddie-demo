package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;
import com.weimob.saas.ec.limitation.common.LimitLevelEnum;
import com.weimob.saas.ec.limitation.common.LimitTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * @author lujialin
 * @description 保存限购商品入参
 * @date 2018/6/4 17:47
 */
public class SaveGoodsLimitInfoRequestVo implements Serializable {

    private static final long serialVersionUID = 8762886494973583461L;
    
    private List<SaveGoodsLimitInfoVo> goodsList;

    public List<SaveGoodsLimitInfoVo> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<SaveGoodsLimitInfoVo> goodsList) {
        this.goodsList = goodsList;
    }
}
