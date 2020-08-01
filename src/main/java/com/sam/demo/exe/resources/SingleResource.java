package com.sam.demo.exe.resources;

import java.util.concurrent.Semaphore;

public abstract class SingleResource<D> implements Resource<D> {

    private Semaphore semaphore = new Semaphore(1);

    @Override
    public void control() throws InterruptedException {
        semaphore.acquire();
    }

    @Override
    public void release() {
        semaphore.release();
    }

}
