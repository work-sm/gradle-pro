package com.sam.demo.perform.actor.impl;

import com.sam.demo.perform.actor.Executor;
import com.sam.demo.perform.script.Story;

import java.io.IOException;

public class SleepWaitExecutor extends Executor {

    private final int millis;

    public SleepWaitExecutor(String path, String name, String[] command) throws IOException {
        this(path, name, command, 1000);
    }

    public SleepWaitExecutor(String path, String name, String[] command, int millis) throws IOException {
        super(path, name, command);
        this.millis = millis;
    }

    @Override
    protected void strategy(Story story) throws Exception {
        Thread.sleep(millis);
    }

}
