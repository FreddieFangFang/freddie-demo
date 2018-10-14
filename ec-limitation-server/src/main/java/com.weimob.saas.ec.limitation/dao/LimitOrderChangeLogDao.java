package com.weimob.saas.ec.limitation.dao;

import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;

import java.util.List;

public interface LimitOrderChangeLogDao {
    /**
     * @title 新增日志
     * @author qi.he
     * @date 2018/10/13 0013 21:21
     * @param [orderChangeLogEntity]
     * @return java.lang.Integer
     */
    Integer insertLog(LimitOrderChangeLogEntity orderChangeLogEntity);

    /**
     * @title 更新日志状态
     * @author qi.he
     * @date 2018/10/13 0013 21:32
     * @param [ticket]
     * @return java.lang.Integer
     */
    Integer updateLogStatusByTicket(String ticket);

    /**
     * @title 通过refer_id，biz_type，service_name查询日志记录
     * @author qi.he
     * @date 2018/10/13 0013 23:01
     * @param [queryLogParameter]
     * @return com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity
     */
    LimitOrderChangeLogEntity getLogByReferId(LimitOrderChangeLogEntity queryLogParameter);

    /**
     * @title 通过Ticket查询日志记录
     * @author qi.he
     * @date 2018/10/13 0013 21:45
     * @param [ticket]
     * @return java.util.List<com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity>
     */
    List<LimitOrderChangeLogEntity> listLogByTicket(String ticket);
 }