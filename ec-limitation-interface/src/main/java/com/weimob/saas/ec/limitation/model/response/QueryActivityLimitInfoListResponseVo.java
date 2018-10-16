package com.weimob.saas.ec.limitation.model.response;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 批量查询活动限购信息出参
 * @Author qi.he
 * @Date 2018-10-16 14:50
 */
public class QueryActivityLimitInfoListResponseVo implements Serializable{

    private static final long serialVersionUID = -6977693087360387878L;

    private Long pid;

    private Long storeId;

    private List<QueryActivityLimitInfoResponseVo> limitInfoVos;

    public QueryActivityLimitInfoListResponseVo() {
    }

    public QueryActivityLimitInfoListResponseVo(Long pid, Long storeId, List<QueryActivityLimitInfoResponseVo> limitInfoVos) {
        this.pid = pid;
        this.storeId = storeId;
        this.limitInfoVos = limitInfoVos;
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

    public List<QueryActivityLimitInfoResponseVo> getLimitInfoVos() {
        return limitInfoVos;
    }

    public void setLimitInfoVos(List<QueryActivityLimitInfoResponseVo> limitInfoVos) {
        this.limitInfoVos = limitInfoVos;
    }
}
