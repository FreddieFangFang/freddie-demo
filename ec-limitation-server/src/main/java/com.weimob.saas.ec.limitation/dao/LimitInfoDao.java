package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.model.LimitParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LimitInfoDao {
    /**
     * @title 保存限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:06
     * @param [entity]
     * @return java.lang.Integer
     */
    Integer insertLimitInfo(LimitInfoEntity entity);

    /**
     * @title 删除限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:06
     * @param [entity]
     * @return java.lang.Integer
     */
    Integer deleteLimitInfo(LimitInfoEntity entity);

    /**
     * @title 更新限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:06
     * @param [entity]
     * @return java.lang.Integer
     */
    Integer updateLimitInfo(LimitInfoEntity entity);

    /**
     * @title 回滚限购信息
     * @author qi.he
     * @date 2018/10/11 0011 15:05
     * @param [limitId]
     * @return java.lang.Integer
     */
    Integer reverseLimitInfoStatus(@Param("limitId") Long limitId);

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
    List<LimitInfoEntity> listLimitInfoByBizId(List<LimitParam> limitParams);

}