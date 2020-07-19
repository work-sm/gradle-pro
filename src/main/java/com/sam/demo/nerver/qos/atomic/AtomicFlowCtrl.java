package com.sam.demo.nerver.qos.atomic;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AtomicFlowCtrl {

    private static final ConcurrentMap<String, AtomicFlowCounter> flowMap = new ConcurrentHashMap<String, AtomicFlowCounter>();
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static final AtomicInteger count = new AtomicInteger(0);

    public static boolean isExceed(String name) {
        AtomicFlowCounter counter = flowMap.get(name);
        if (counter != null) {
            return !counter.incCounter();
        }
        return true;
    }

    public static boolean releaseCounter(String name) {
        AtomicFlowCounter counter = flowMap.get(name);
        if (counter != null) {
            return counter.decCounter();
        }
        return false;
    }

    public static int getMaxFlow(String name) {
        AtomicFlowCounter counter = flowMap.get(name);
        if (counter != null) {
            return counter.getMaxflow();
        }
        return 0;
    }

    public static void setMaxflow(String name, int maxflow) {
        AtomicFlowCounter counter = null;
        lock.lock();
        try {
            counter = flowMap.get(name);
            if (counter == null) {
                counter = new AtomicFlowCounter(maxflow, name);
                flowMap.put(name, counter);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        AtomicFlowCtrl.setMaxflow("b2b", 50);
        ExecutorService cacheThreadPool = Executors.newFixedThreadPool(100, new ThreadFactory() {
            private ThreadGroup threadGroup = System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup();
            private AtomicLong seq = new AtomicLong(0);
            private String THREADNAME = "AtomicFlowControlManager-Thread-";

            @Override
            public Thread newThread(Runnable r) {
                Thread threadAdapter = new Thread(threadGroup, r, THREADNAME + seq.getAndIncrement(), 0);
                return threadAdapter;
            }
        });
        long t1 = System.currentTimeMillis();
        final CountDownLatch cdl = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            cacheThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 1000笔业务1s内不定时到来
                        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    boolean isExceed = AtomicFlowCtrl.isExceed("b2b");

                    // 等待0.1s
                    if (isExceed) {
                        System.out.println(Thread.currentThread() + "超出流量等待最多0.1s");
                        lock.lock();
                        try {
                            condition.await(100, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }
                        isExceed = AtomicFlowCtrl.isExceed("b2b");
                        if (!isExceed) {
                            // 睡眠300ms模拟服务时间
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(Thread.currentThread() + "执行服务成功");
                            AtomicFlowCtrl.releaseCounter("b2b");
                            lock.lock();
                            try {
                                condition.signal();
                            } finally {
                                lock.unlock();
                            }
                            count.incrementAndGet();
                        }
                    } else {
                        // 睡眠200ms模拟服务时间
                        long t1 = System.currentTimeMillis();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        long t2 = System.currentTimeMillis();
                        System.out.println(Thread.currentThread() + "执行服务成功耗时[" + (t2 - t1) + "]");
                        AtomicFlowCtrl.releaseCounter("b2b");
                        lock.lock();
                        try {
                            condition.signal();
                        } finally {
                            lock.unlock();
                        }
                        count.incrementAndGet();
                    }
                    cdl.countDown();
                }
            });
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        cacheThreadPool.shutdown();
        long t2 = System.currentTimeMillis();
        System.out.println("耗时： " + (t2 - t1) + " 业务成功笔数： " + count.get() + "  " + (count.get() * 1000 / (t2 - t1))
                + " 笔/秒" + "  业务处理成功率： " + (count.get() * 100 / 1000.0D) + "%");
    }
}