package com.weimob.saas.ec.limitation.entity;

import java.util.Date;

public class UserGoodsLimitEntity implements Comparable<UserGoodsLimitEntity> {
    private Long id;

    private Long pid;

    private Long storeId;

    private Long wid;

    private Long limitId;

    private Long goodsId;

    private Integer buyNum;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getWid() {
        return wid;
    }

    public void setWid(Long wid) {
        this.wid = wid;
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

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public int compareTo(UserGoodsLimitEntity o) {
        int result = this.getWid().intValue() - o.getWid().intValue();
        if (result != 0) {
            return result;
        } else {
            result = this.getLimitId().intValue() - o.getLimitId().intValue();
            if (result != 0) {
                return result;
            } else {
                return this.getGoodsId().intValue() - o.getGoodsId().intValue();
            }
        }
    }
}