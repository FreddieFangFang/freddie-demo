package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;

import java.util.List;

/**
 * @author lujialin
 * @description 保存限购信息入参
 * @date 2018/5/29 10:26
 */
public class LimitationInfoRequestVo extends BaseRequest{

    private static final long serialVersionUID = 4186174150414792157L;

    /**
     * 限购主表Id
     */
    private Long limitId;
    /**
     * 门店id集合
     */
    private List<Long> storeIdList;
    /**
     * 限购渠道类型
     */
    private String channelType;
    /**
     * 限购来源
     */
    private String source;
    /**
     * 限购级别
     */
    private Byte limitLevel;
    /**
     * 活动id或者商品id
     */
    private Long bizId;
    /**
     * 活动类型或者商品限购类型
     */
    private Byte bizType;
    /**
     * 限购对每个人还是所有人
     */
    private Byte limitType;
    /**
     * 限购数量
     */
    private Integer limitNum;

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }

    public List<Long> getStoreIdList() {
        return storeIdList;
    }

    public void setStoreIdList(List<Long> storeIdList) {
        this.storeIdList = storeIdList;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Byte getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(Byte limitLevel) {
        this.limitLevel = limitLevel;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public Byte getLimitType() {
        return limitType;
    }

    public void setLimitType(Byte limitType) {
        this.limitType = limitType;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }
}
