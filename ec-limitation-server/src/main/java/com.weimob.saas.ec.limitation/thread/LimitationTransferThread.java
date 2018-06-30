package com.weimob.saas.ec.limitation.thread;

/**
 * @author lujialin
 * @description 执行数据迁移线程
 * @date 2018/6/30 10:36
 */
public class LimitationTransferThread implements Runnable {

    private int limitationNum;

    public LimitationTransferThread(int limitationNum) {
        this.limitationNum = limitationNum;
    }

    @Override
    public void run() {

    }
}
