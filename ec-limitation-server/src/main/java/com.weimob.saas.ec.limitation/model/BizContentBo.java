package com.weimob.saas.ec.limitation.model;

/**
 * @Description 业务信息对象
 * @Author qi.he
 * @Date 2019-01-17 22:34
 */
public class BizContentBo {
    /** 规则数量 */
    private Integer ruleNum;

    /** 活动参与次数 */
    private Integer participateTime;

    /** 维权单号 */
    private Long rightsNo;

    /** 维权商品数量 */
    private Integer rightsGoodsNum;

    /** 限时折扣二级分类 */
    private Integer activityStockType;

    public BizContentBo() {
    }

    public BizContentBo(Integer participateTime) {
        this.participateTime = participateTime;
    }

    public BizContentBo(Integer ruleNum, Integer participateTime) {
        this.ruleNum = ruleNum;
        this.participateTime = participateTime;
    }

    public BizContentBo(Long rightsNo, Integer rightsGoodsNum, Integer participateTime) {
        this.rightsNo = rightsNo;
        this.rightsGoodsNum = rightsGoodsNum;
        this.participateTime = participateTime;
    }

    public Integer getRuleNum() {
        return ruleNum;
    }

    public void setRuleNum(Integer ruleNum) {
        this.ruleNum = ruleNum;
    }

    public Integer getParticipateTime() {
        return participateTime;
    }

    public void setParticipateTime(Integer participateTime) {
        this.participateTime = participateTime;
    }

    public Long getRightsNo() {
        return rightsNo;
    }

    public void setRightsNo(Long rightsNo) {
        this.rightsNo = rightsNo;
    }

    public Integer getRightsGoodsNum() {
        return rightsGoodsNum;
    }

    public void setRightsGoodsNum(Integer rightsGoodsNum) {
        this.rightsGoodsNum = rightsGoodsNum;
    }

    public Integer getActivityStockType() {
        return activityStockType;
    }

    public void setActivityStockType(Integer activityStockType) {
        this.activityStockType = activityStockType;
    }
}
