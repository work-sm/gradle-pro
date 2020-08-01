package com.sam.demo.exe.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger poolNumber = new AtomicInteger();

    private final String name;

    NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, name + poolNumber.getAndIncrement());
    }

}