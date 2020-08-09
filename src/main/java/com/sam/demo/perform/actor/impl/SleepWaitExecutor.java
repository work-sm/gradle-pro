package com.sam.demo.perform.actor.impl;

import com.sam.demo.perform.actor.Executor;

import java.io.IOException;

public class SleepWaitExecutor extends Executor {

    private final int millis;

    public SleepWaitExecutor(String path, String name) throws IOException {
        this(path, name, 1000);
    }

    public SleepWaitExecutor(String path, String name, int millis) throws IOException {
        super(path, name);
        this.millis = millis;
    }

    @Override
    protected void strategy() throws Exception {
        Thread.sleep(millis);
    }

}
