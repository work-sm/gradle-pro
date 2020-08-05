package com.sam.demo.exe.resources;

import java.util.concurrent.Semaphore;

public abstract class SingleResource implements Resource {

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
