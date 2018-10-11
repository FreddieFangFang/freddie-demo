package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LimitInfoDao extends BaseDao<LimitInfoEntity> {

    /**
     * @title 新增限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:06
     * @param [entity]
     * @return java.lang.Long
     */
    Long insertLimitInfo(LimitInfoEntity entity);

    /**
     * @title 删除限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:06
     * @param [entity]
     * @return void
     */
    void deleteLimitInfo(LimitInfoEntity entity);

    /**
     * @title 更新限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:06
     * @param [entity]
     * @return java.lang.Long
     */
    Long updateLimitInfo(LimitInfoEntity entity);

    /**
     * @title 获取指定限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:07
     * @param [limitParam]
     * @return com.weimob.saas.ec.limitation.entity.LimitInfoEntity
     */
    LimitInfoEntity getLimitInfo(LimitParam limitParam);

    /**
     * @title 批量查询限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:07
     * @param [limitParams]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.LimitInfoEntity>
     */
    List<LimitInfoEntity> listLimitInfo(List<LimitParam> limitParams);

    /**
     * @title 回滚限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:05
     * @param [limitId]
     * @return void
     */
    void reverseLimitInfoStatus(@Param("limitId") Long limitId);
}