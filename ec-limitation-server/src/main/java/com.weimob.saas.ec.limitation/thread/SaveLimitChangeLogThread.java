package com.weimob.saas.ec.limitation.thread;

import com.alibaba.dubbo.rpc.RpcContext;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.LimitOrderChangeLogDao;
import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;

import java.util.List;

/**
 * @author lujialin
 * @description 保存下单记录
 * @date 2018/6/7 15:31
 */
public class SaveLimitChangeLogThread implements Runnable {
    private LimitOrderChangeLogDao limitOrderChangeLogDao;

    private List<LimitOrderChangeLogEntity> logEntityList;

    private RpcContext rpcContext;
    public SaveLimitChangeLogThread() {

    }
    // 影子库异步线程传入tiket写入影子库 传入globalTicket
    public SaveLimitChangeLogThread(LimitOrderChangeLogDao limitOrderChangeLogDao, List<LimitOrderChangeLogEntity> logEntityList, RpcContext rpcContext) {
        this.limitOrderChangeLogDao = limitOrderChangeLogDao;
        this.logEntityList = logEntityList;
        this.rpcContext = rpcContext;
    }

    public SaveLimitChangeLogThread(LimitOrderChangeLogDao limitOrderChangeLogDao, List<LimitOrderChangeLogEntity> logEntityList) {
        this.limitOrderChangeLogDao = limitOrderChangeLogDao;
        this.logEntityList = logEntityList;
    }

    public LimitOrderChangeLogDao getLimitOrderChangeLogDao() {
        return limitOrderChangeLogDao;
    }

    public void setLimitOrderChangeLogDao(LimitOrderChangeLogDao limitOrderChangeLogDao) {
        this.limitOrderChangeLogDao = limitOrderChangeLogDao;
    }

    public List<LimitOrderChangeLogEntity> getLogEntityList() {
        return logEntityList;
    }

    public void setLogEntityList(List<LimitOrderChangeLogEntity> logEntityList) {
        this.logEntityList = logEntityList;
    }

    @Override
    public void run() {
        // 日志信息写入影子库
        for (LimitOrderChangeLogEntity orderChangeLogEntity : logEntityList) {
            try {
                RpcContext.getContext().setGlobalTicket(rpcContext.getGlobalTicket());
                RpcContext.getContext().setRpcId(LimitConstant.DEFAULT_RPC_ID);
                limitOrderChangeLogDao.insert(orderChangeLogEntity);
            } catch (Exception e) {
                throw new LimitationBizException(LimitationErrorCode.SQL_INSERT_ORDER_LOG_ERROR);
            }
        }
    }
}
