package com.weimob.saas.ec.limitation.handler.biz;

import com.alibaba.dubbo.rpc.RpcContext;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.LimitOrderChangeLogDao;
import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.Handler;
import com.weimob.saas.ec.limitation.handler.ReverseLimitHandlerFactory;
import com.weimob.saas.ec.limitation.model.request.ReverseUserLimitRequestVo;
import com.weimob.saas.ec.limitation.utils.LimitationRedisClientUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lujialin
 * @description 限购回滚handler
 * @date 2018/6/6 14:36
 */
@Service("reverseUserLimitHandler")
public class ReverseUserLimitHandler {

    private static Logger LOGGER = Logger.getLogger(ReverseUserLimitHandler.class);

    @Autowired
    private LimitOrderChangeLogDao limitOrderChangeLogDao;
    @Autowired
    private ReverseLimitHandlerFactory reverseLimitHandlerFactory;

    public void reverse(String ticket) {
        //1.查询回滚订单日志表
        List<LimitOrderChangeLogEntity> orderChangeLogEntityList = null;
        try {
            orderChangeLogEntityList = limitOrderChangeLogDao.queryOrderChangeLogListByTicket(ticket);
        } catch (Exception e) {
            LOGGER.error("fail to query order entity list", e);
            throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_ORDER_CHANGE_LOG_ERROR, e);
        }

        if (CollectionUtils.isEmpty(orderChangeLogEntityList)) {

            throw new LimitationBizException(LimitationErrorCode.INVALID_REVERSE_TICKET);
        }

        //2.服务名
        String serviceName = orderChangeLogEntityList.get(0).getServiceName();
        Handler<?> handler = reverseLimitHandlerFactory.getHandlerByServiceName(serviceName);
        handler.doReverse(orderChangeLogEntityList);

        //3.修改订单日志表
        int updateResult = 0;

        try {
            updateResult = limitOrderChangeLogDao.updateOrderChangeLogListToReversed(ticket);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_ORDER_CHANGE_LOG_ERROR, e);
        }
        if (updateResult == 0) {
            throw new LimitationBizException(LimitationErrorCode.SQL_UPDATE_ORDER_CHANGE_LOG_ERROR);
        }
    }

    public void reverseFromCacheQueue(Integer reversePopSize) {
        List<Object> objectList = LimitationRedisClientUtils.popDataFromQueen(LimitConstant.KEY_LIMITATION_REVERSE_QUEUE, reversePopSize);
        if (CollectionUtils.isEmpty(objectList)) {
            return;
        }
        List<String> ticketList = new ArrayList<>(objectList.size());
        for (Object obj : objectList){
            ticketList.add((String)obj);
        }
        for(String ticket : ticketList){
            try {
                RpcContext.getContext().setGlobalTicket(ticket);
                reverse(ticket);
            } catch (Exception e){
                // 如果异步订单回滚日志表没有查询到 就将ticket写入队列后边
                LimitationRedisClientUtils.pushDataToQueue(LimitConstant.KEY_LIMITATION_REVERSE_QUEUE, ticket);
                LOGGER.error("fail to reserve limitation list", e);
            }
        }
    }
}
