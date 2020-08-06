package com.sam.demo.exe.resources.impl;

import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.resources.Executor;

import java.io.IOException;

public class SleepWaitProcedure<D extends Carrier> extends Executor<D> {

    private final int millis;

    public SleepWaitProcedure(String path, String command, int millis) throws IOException {
        super(path, command);
        this.millis = millis;
    }

    @Override
    protected void strategy() throws Exception {
        Thread.sleep(millis);
    }

}
