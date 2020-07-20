package com.sam.demo.process.exec;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Semaphore;

public class TleParams {

    private Semaphore semaphore = new Semaphore(0);

    private File tle;

    public TleParams(File tle) {
        this.tle = tle;
    }

    public void params(String params) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(tle, "rw");
        try {
            raf.setLength(0);
            raf.seek(0);
            raf.write(params.getBytes());
        } finally {
            if (raf != null) {
                raf.close();
            }
            semaphore.release();
        }
    }

    public void waitFor() throws InterruptedException {
        semaphore.acquire();
    }

}
