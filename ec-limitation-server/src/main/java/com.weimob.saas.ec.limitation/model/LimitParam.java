package com.weimob.saas.ec.limitation.model;

/**
 * @author lujialin
 * @description 数据库查询入参
 * @date 2018/5/29 17:14
 */
public class LimitParam {
    private Long pid;
    private Long bizId;
    private Integer bizType;
    private Long limitId;
    private Long goodsId;
    private Integer deleted;

    public LimitParam() {

    }

    public LimitParam(Long pid, Long limitId) {
        this.pid = pid;
        this.limitId = limitId;
    }

    public LimitParam(Long pid, Long limitId, Long goodsId) {
        this.pid = pid;
        this.limitId = limitId;
        this.goodsId = goodsId;
    }

    public LimitParam(Long pid, Long bizId, Integer bizType) {
        this.pid = pid;
        this.bizId = bizId;
        this.bizType = bizType;
    }

    public LimitParam(Long pid, Long bizId, Integer bizType, Integer deleted) {
        this.pid = pid;
        this.bizId = bizId;
        this.bizType = bizType;
        this.deleted = deleted;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
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

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
