package com.freddie.demo.javabase.thread;


public class ThreadSafeSample {
    public int sharedState;

    public void nonSafeAction() {
        while (sharedState < 100000) {
//            System.out.println("sharedState is:"+sharedState);
            //将两次赋值过程用 synchronized 保护起来，使用 this 作为互斥单元，就可以避免别的线程并发的去修改 sharedState。
//            synchronized (this){
                int former = sharedState++;
                int latter = sharedState;
                if (former != latter - 1) {
                    System.out.printf("Observed data race, former is " +
                            former + ", " + "latter is " + latter);
                    break;
                }
//            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeSample sample = new ThreadSafeSample();
        Thread threadA = new Thread() {
            public void run() {
                sample.nonSafeAction();
            }
        };
        Thread threadB = new Thread() {
            public void run() {
                sample.nonSafeAction();
            }
        };
        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();
    }
}