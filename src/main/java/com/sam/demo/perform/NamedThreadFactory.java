package com.sam.demo.perform;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger poolNumber = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "new" + poolNumber.getAndIncrement());
    }

}
