package com.sam.demo.exe.procedures;

public abstract class RunnerProcedure implements Procedure {

    private volatile boolean running = true;

    @Override
    public void run(){
        while (running){
            process();
        }
        close();
    }

    @Override
    public void destroy() {
        running = false;
    }

    protected abstract void process();

    protected abstract void close();

}
