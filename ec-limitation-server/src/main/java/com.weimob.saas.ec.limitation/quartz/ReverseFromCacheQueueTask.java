package com.weimob.saas.ec.limitation.quartz;

import com.dianping.cat.Cat;
import com.weimob.saas.ec.common.util.SoaUtil;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.handler.biz.ReverseUserLimitHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReverseFromCacheQueueTask {

	private static final Logger log = LoggerFactory.getLogger(ReverseFromCacheQueueTask.class);

	/** 定时任务开关 */
	private Integer taskSwitch;
	/** 指定ip运行定时任务 */
	private String runTaskIp;
	/** 定时任务运行状态标志 */
	private static AtomicBoolean isReverseFromCacheQueue = new AtomicBoolean(false);
	/** 从缓存一次pop的ticket个数*/
	private Integer reversePopSize;

	/** 操作影子库还是真实库 标志*/
	private Integer stress;

	private ReverseUserLimitHandler reverseUserLimitHandler;


	

	public void reverseFromCacheQueue() {
		if(taskSwitch == LimitConstant.LIMITATION_REVERSE_TASK_OFF ){
			return;
		}
		if (!SoaUtil.getLocalIp().equals(runTaskIp)) {
			return;
		}
		if (isReverseFromCacheQueue.compareAndSet(false, true)) {
			try {
				reverseUserLimitHandler.reverseFromCacheQueue(reversePopSize);
			} catch (Exception e) {
				log.error("reverseFromCacheQueueTask error", e);
			} finally {
				isReverseFromCacheQueue.compareAndSet(true, false);
			}
		}
//		else {
//			Cat.logError("reverseFromCacheQueueTask run with conflict, because last reverseFromCacheQueueTask is running",new Exception());
//		}
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

	public Integer getReversePopSize() {
		return reversePopSize;
	}

	public void setReversePopSize(Integer reversePopSize) {
		this.reversePopSize = reversePopSize;
	}

	public ReverseUserLimitHandler getReverseUserLimitHandler() {
		return reverseUserLimitHandler;
	}

	public void setReverseUserLimitHandler(ReverseUserLimitHandler reverseUserLimitHandler) {
		this.reverseUserLimitHandler = reverseUserLimitHandler;
	}

	public Integer getStress() {
		return stress;
	}

	public void setStress(Integer stress) {
		this.stress = stress;
	}
}
