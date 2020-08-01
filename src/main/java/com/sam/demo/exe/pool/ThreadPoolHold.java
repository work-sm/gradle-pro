package com.sam.demo.exe.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolHold {

    private ExecutorService pool;

    private ThreadPoolHold() {
        NamedThreadFactory factory = new NamedThreadFactory("procedure");
        pool = Executors.newCachedThreadPool(factory);
    }

    private static class SingletonInstance {
        private static final ThreadPoolHold INSTANCE = new ThreadPoolHold();
    }

    public static ExecutorService getInstance() {
        return SingletonInstance.INSTANCE.pool;
    }

}
