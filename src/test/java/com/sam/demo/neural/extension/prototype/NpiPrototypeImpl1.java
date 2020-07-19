package com.sam.demo.neural.extension.prototype;

import com.sam.demo.neural.extension.Extension;

import java.util.concurrent.atomic.AtomicLong;

@Extension("spiPrototypeImpl1")
public class NpiPrototypeImpl1 implements NpiPrototype {
    private static AtomicLong counter = new AtomicLong(0);
    private long index = 0;

    public NpiPrototypeImpl1() {
        index = counter.incrementAndGet();
    }

    @Override
    public long spiHello() {
        return index;
    }

}
