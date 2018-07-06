package com.weimob.saas.ec.limitation.entity;

import java.util.Date;

public class LimitInfoEntity {
    /**
     * 限购主表Id
     */
    private Long limitId;
    /**
     * 商品id
     */
    private Long pid;
    /**
     * 限购渠道类型
     */
    private String saleChannelType;
    /**
     * 限购来源
     */
    private String source;
    /**
     * 限购级别
     */
    private Integer limitLevel;
    /**
     * 活动id或者商品id
     */
    private Long bizId;
    /**
     * 活动类型或者商品限购类型
     */
    private Integer bizType;
    /**
     * 限购对每个人还是所有人
     */
    private Integer limitType;
    /**
     * 限购数量
     */
    private Integer limitNum;
    /**
     * 已售数量
     */
    private Integer soldNum;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getSaleChannelType() {
        return saleChannelType;
    }

    public void setSaleChannelType(String saleChannelType) {
        this.saleChannelType = saleChannelType == null ? null : saleChannelType.trim();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getSoldNum() {
        return soldNum;
    }

    public void setSoldNum(Integer soldNum) {
        this.soldNum = soldNum;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Integer getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(Integer limitLevel) {
        this.limitLevel = limitLevel;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Integer getLimitType() {
        return limitType;
    }

    public void setLimitType(Integer limitType) {
        this.limitType = limitType;
    }
}