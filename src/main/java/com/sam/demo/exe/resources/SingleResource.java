package com.sam.demo.exe.resources;

import com.sam.demo.exe.data.Carrier;

import java.util.concurrent.Semaphore;

public abstract class SingleResource<D extends Carrier> implements Resource<D> {

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
