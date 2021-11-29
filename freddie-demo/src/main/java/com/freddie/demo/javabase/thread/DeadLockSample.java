package com.freddie.demo.javabase.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 死锁的简单实例
 */
public class DeadLockSample extends Thread {
    private String first;
    private String second;

    public DeadLockSample(String name, String first, String second) {
        super(name);
        this.first = first;
        this.second = second;
    }

    public void run() {
        synchronized (first) {
            System.out.println(this.getName() + " obtained: " + first);
            try {
                Thread.sleep(100000L);
                synchronized (second) {
                    System.out.println(this.getName() + " obtained: " + second);
                }
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
    }

    //简单死锁实例1，使用jstack定位死锁
//    public static void main(String[] args) throws InterruptedException {
//        String lockA = "lockA";
//        String lockB = "lockB";
//        DeadLockSample t1 = new DeadLockSample("Thread1", lockA, lockB);
//        DeadLockSample t2 = new DeadLockSample("Thread2", lockB, lockA);
//        t1.start();
//        t2.start();
//        t1.join();
//        t2.join();
//    }

    //使用ThreadMXBean.findDeadlockedThreads​()定位死锁
    public static void main(String[] args) throws InterruptedException {
        ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
        Runnable dlCheck = new Runnable() {
            @Override
            public void run() {
                long[] threadIds = mbean.findDeadlockedThreads();
                if (threadIds != null) {
                    ThreadInfo[] threadInfos = mbean.getThreadInfo(threadIds);
                    System.out.println("Detected deadlock threads:");
                    for (ThreadInfo threadInfo : threadInfos) {
                        System.out.println(threadInfo.getThreadName());
                    }
                }
            }
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // 稍等5秒，然后每10秒进行一次死锁扫描
        scheduler.scheduleAtFixedRate(dlCheck, 5L, 10L, TimeUnit.SECONDS);
        // 死锁样例代码…
        String lockA = "lockA";
        String lockB = "lockB";
        DeadLockSample t1 = new DeadLockSample("Thread1", lockA, lockB);
        DeadLockSample t2 = new DeadLockSample("Thread2", lockB, lockA);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }


    /*// Thread HttpClient-6-SelectorManager:
    readLock.lock();
    writeLock.lock();

    // 持有readLock/writeLock，调用close（）需要获得closeLock
    close();

    // Thread HttpClient-6-Worker-2 持有closeLock
    implCloseSelectableChannel(); //想获得readLock*/

}
