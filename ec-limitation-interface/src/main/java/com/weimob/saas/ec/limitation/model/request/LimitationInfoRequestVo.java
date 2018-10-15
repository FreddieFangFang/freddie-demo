package com.weimob.saas.ec.limitation.model.request;

import com.weimob.saas.ec.common.request.BaseRequest;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitLevelEnum;
import com.weimob.saas.ec.limitation.common.LimitTypeEnum;

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
    private String saleChannelType;
    /**
     * 限购来源
     */
    private String source;
    /**
     * 限购级别，取值范围，参见：{@link com.weimob.saas.ec.limitation.common.LimitLevelEnum}
     */
    private Integer limitLevel;
    /**
     * 活动id或者商品id
     */
    private Long bizId;
    /**
     * 活动类型或者商品限购类型，取值范围，参见：{@link com.weimob.saas.ec.limitation.common.LimitBizTypeEnum}
     */
    private Integer bizType;
    /**
     * 限购对每个人还是所有人，取值范围，参见：{@link com.weimob.saas.ec.limitation.common.LimitTypeEnum}
     */
    private Integer limitType;
    /**
     * 限购数量
     */
    private Integer limitNum;
    /**
     * 选择门店类型(1:全部门店 2:部分门店;3:部分排除)
     */
    private Integer selectStoreType;

    /** 可售数量相关信息-目前只有优惠套装才会传 */
    private ThresholdInfo thresholdInfo;

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

    public String getSaleChannelType() {
        return saleChannelType;
    }

    public void setSaleChannelType(String saleChannelType) {
        this.saleChannelType = saleChannelType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
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

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getSelectStoreType() {
        return selectStoreType;
    }

    public void setSelectStoreType(Integer selectStoreType) {
        this.selectStoreType = selectStoreType;
    }

    public ThresholdInfo getThresholdInfo() {
        return thresholdInfo;
    }

    public void setThresholdInfo(ThresholdInfo thresholdInfo) {
        this.thresholdInfo = thresholdInfo;
    }
}
