package com.sam.demo.perform.actor;

import java.util.concurrent.Semaphore;

public abstract class SingleActor implements Actor {

    private Semaphore semaphore = new Semaphore(1);

    @Override
    public void invite() throws Exception {
        semaphore.acquire();
    }

    @Override
    public void release() throws Exception {
        semaphore.release();
    }

}
