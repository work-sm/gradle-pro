package com.sam.demo.exe.procedures;

import com.sam.demo.exe.data.Carrier;

public class SleepWaitProcedure<D extends Carrier> extends BaseProcedure<D> {

    private final int millis;

    public SleepWaitProcedure(int millis){
        this.millis = millis;
    }

    @Override
    protected void strategy() throws Exception {
        Thread.sleep(millis);
    }

}
