package com.freddie.demo.javabase.thread;

public class TestThread {

    public void fun001() {
        Thread daemonThread = new Thread();
        //守护线程：有的时候应用中需要一个长期驻留的服务程序，但是不希望其影响应用退出，就可以将其设置为守护线程，如果 JVM 发现只有守护线程存在时，将结束进程
        daemonThread.setDaemon(true);
        daemonThread.start();
    }

    //写一个最简单的打印 HelloWorld 的程序，说说看，运行这个应用，Java 至少会创建几个线程呢？
    //使用了两种方式获取当前程序的线程数。
    //1、使用线程管理器MXBean
    //2、直接通过线程组的activeCount
    //第二种需要注意不断向上找父线程组，否则只能获取当前线程组，结果是1
    //结论:
    //使用以上两种方式获取的线程总数都是5个。
    //main
    //Attach Listener
    //Signal Dispatcher
    //Finalizer
    //Reference Handler
    //此外，如果使用的IDE是IDEA 直接运行会多一个Monitor Ctrl-break线程，这个是IDE的原因。debug模式下不会有这个线程。
    private static class fun {
        private static void fun002() {
            System.out.println("hello world");
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            ThreadGroup topGroup = group;
            while (group != null) {
                topGroup = group;
                group = group.getParent();
            }
            int nowThreads = topGroup.activeCount();
            Thread[] lstThreads = new Thread[nowThreads];
            topGroup.enumerate(lstThreads);
            for (int i = 0; i < nowThreads; i++) {
                System.out.println("线程number：" + i + " = " + lstThreads[i].getName());
            }
        }
    }

    public static void main(String[] args) {
        fun.fun002();
    }
}
