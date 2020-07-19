package com.sam.demo.common;

import com.sam.demo.nerver.common.SystemClock;

import java.util.concurrent.CountDownLatch;

/**
 *
 */
public class CurrentTimeMillisTest {

    private static final int COUNT = 1000;

    public static void main(String[] args) throws Exception {
        long parallelCalls = parallelCalls();
        //这个时间包含了线程切换
        System.out.println("100 currentTimeMillis parallel calls: " + parallelCalls + " ns");

        long clockCalls = clockCalls();
        System.out.println("100 clockCalls parallel calls: " + clockCalls + " ns");

        System.out.println("100 clockCalls parallel calls: " + (clockCalls - parallelCalls) + " ns");
    }

    private static long parallelCalls() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(COUNT);
        for (int i = 0; i < COUNT; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        //并未上锁，只是准确计时，从这里开始
        long beginTime = nanoTime();
        //线程开始运行
        startLatch.countDown();
        //等待所有线程完成
        endLatch.await();
        return nanoTime() - beginTime;
    }

    private static long clockCalls() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(COUNT);
        for (int i = 0; i < COUNT; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    now();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        long beginTime = nanoTime();
        startLatch.countDown();
        endLatch.await();
        return nanoTime() - beginTime;
    }

    private static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private static long now() {
        return SystemClock.now();
    }

    private static long nanoTime() {
        return System.nanoTime();
    }

}
