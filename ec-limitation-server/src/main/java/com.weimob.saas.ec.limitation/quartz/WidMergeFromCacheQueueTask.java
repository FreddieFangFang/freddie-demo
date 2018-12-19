package com.weimob.saas.ec.limitation.quartz;

import com.alibaba.dubbo.rpc.RpcContext;
import com.dianping.cat.Cat;
import com.weimob.saas.ec.common.util.SoaUtil;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.facade.UserLimitUpdateFacadeService;
import com.weimob.saas.ec.limitation.utils.LimitationRedisClientUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: fei.zheng
 * @Date: 2018/12/19 00:01
 * @Description:
 */
public class WidMergeFromCacheQueueTask {

    private static final Logger log = LoggerFactory.getLogger(WidMergeFromCacheQueueTask.class);

    /**
     * 定时任务开关
     */
    private Integer taskSwitch;
    /**
     * 指定ip运行定时任务
     */
    private String runTaskIp;
    /**
     * 定时任务运行状态标志
     */
    private static AtomicBoolean isReverseFromCacheQueue = new AtomicBoolean(false);
    /**
     * 从缓存一次pop的ticket个数
     */
    private Integer reversePopSize;

    private UserLimitUpdateFacadeService userLimitUpdateFacadeService;

    public void widMergeFromCacheQueue() {
        if (taskSwitch == LimitConstant.LIMITATION_WIDMERGE_TASK_OFF) {
            return;
        }
        if (!SoaUtil.getLocalIp().equals(runTaskIp)) {
            return;
        }
        if (isReverseFromCacheQueue.compareAndSet(false, true)) {
            try {
                List<Object> objectList = LimitationRedisClientUtils.popDataFromQueen(LimitConstant.KEY_LIMITATION_WIDMERGE_QUEUE, reversePopSize);
                if (CollectionUtils.isEmpty(objectList)) {
                    return;
                }
                List<String> valueList = new ArrayList<>(objectList.size());
                for (Object obj : objectList) {
                    valueList.add((String) obj);
                }
                for (String value : valueList) {
                    String[] values = value.split("_");
                    Long pid = Long.parseLong(values[0]);
                    Long newWid = Long.parseLong(values[1]);
                    Long oldWid = Long.parseLong(values[2]);
                    Long time = Long.parseLong(values[3]);
                    Long current = System.currentTimeMillis();
                    if ((current - time) > LimitConstant.WIDMERGE_EXPIRE) {
                        Cat.logTransaction("saas.ec-limitation-service",
                                "WidMergeExceptionLog",
                                "WidMergeFromCacheQueueTask.widMergeFromCacheQueue",
                                System.currentTimeMillis(),
                                value
                        );
                        return;
                    }
                    try {
                        RpcContext.getContext().setGlobalTicket(value);
                        RpcContext.getContext().setRpcId(LimitConstant.DEFAULT_RPC_ID);
                        userLimitUpdateFacadeService.mergeUserLimit(pid, newWid, oldWid);
                        userLimitUpdateFacadeService.mergeUserGoodsLimit(pid, newWid, oldWid);
                    } catch (Exception e) {
                        //如果异常，放回redis
                        LimitationRedisClientUtils.pushDataToQueue(LimitConstant.KEY_LIMITATION_WIDMERGE_QUEUE, value);
                        log.error("widMergeFromCacheQueue异常：" + value);
                    }
                }
            } catch (Exception e) {
                log.error("widMergeFromCacheQueueTask error", e);
            } finally {
                isReverseFromCacheQueue.compareAndSet(true, false);
            }
        }
    }

    public Integer getTaskSwitch() {
        return taskSwitch;
    }

    public void setTaskSwitch(Integer taskSwitch) {
        this.taskSwitch = taskSwitch;
    }

    public String getRunTaskIp() {
        return runTaskIp;
    }

    public void setRunTaskIp(String runTaskIp) {
        this.runTaskIp = runTaskIp;
    }

    public static AtomicBoolean getIsReverseFromCacheQueue() {
        return isReverseFromCacheQueue;
    }

    public static void setIsReverseFromCacheQueue(AtomicBoolean isReverseFromCacheQueue) {
        WidMergeFromCacheQueueTask.isReverseFromCacheQueue = isReverseFromCacheQueue;
    }

    public Integer getReversePopSize() {
        return reversePopSize;
    }

    public void setReversePopSize(Integer reversePopSize) {
        this.reversePopSize = reversePopSize;
    }

    public UserLimitUpdateFacadeService getUserLimitUpdateFacadeService() {
        return userLimitUpdateFacadeService;
    }

    public void setUserLimitUpdateFacadeService(UserLimitUpdateFacadeService userLimitUpdateFacadeService) {
        this.userLimitUpdateFacadeService = userLimitUpdateFacadeService;
    }
}
