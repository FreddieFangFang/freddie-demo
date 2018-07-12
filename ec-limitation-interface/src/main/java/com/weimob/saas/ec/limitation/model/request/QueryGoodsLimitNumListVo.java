package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

/**
 * @author lujialin
 * @description 批量查询商品限购数量
 * @date 2018/6/11 15:42
 */
public class QueryGoodsLimitNumListVo extends BaseRequest {

    private Long goodsId;

    private Long bizId;

    private Integer bizType;

    /**
     * 限时折扣1.冻结库存；2.可用sku
     */
    private Integer activityStockType;
    /**
     * 活动过期查看商品传1，其他传0
     */
    private Integer checkDeleteActivityGoods;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

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

    public Integer getActivityStockType() {
        return activityStockType;
    }

    public void setActivityStockType(Integer activityStockType) {
        this.activityStockType = activityStockType;
    }

    public Integer getCheckDeleteActivityGoods() {
        return checkDeleteActivityGoods;
    }

    public void setCheckDeleteActivityGoods(Integer checkDeleteActivityGoods) {
        this.checkDeleteActivityGoods = checkDeleteActivityGoods;
    }
}
