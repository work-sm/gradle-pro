package com.sam.demo.process.console;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

@Slf4j
public class ReaderConsole implements Closeable {

    private Semaphore semaphore = new Semaphore(0);

    private CyclicBarrier inputBarrier;
    private CyclicBarrier errorBarrier;

    private BufferedReader inputReader;
    private BufferedReader errorReader;
    private String sign;

    private Boolean state;
    private volatile boolean shutdown;

    public ReaderConsole(InputStream input, InputStream error, String sign) {
        InputStreamReader inputStream = new InputStreamReader(input, Charset.forName("GBK"));
        inputReader = new BufferedReader(inputStream);
        InputStreamReader errorStream = new InputStreamReader(error, Charset.forName("GBK"));
        errorReader = new BufferedReader(errorStream);
        this.sign = sign;
        init();
        steam();
        shutdown = false;
    }

    private void init() {
        inputBarrier = new CyclicBarrier(1, () -> {
            log.info("完成");
            state = true;
            semaphore.release();
        });
        errorBarrier = new CyclicBarrier(1, () -> {
            log.info("异常");
            state = false;
            semaphore.release();
        });
    }

    private void steam() {
        Thread inputSteam = new Thread(() -> {
            while (!shutdown) {
                try {
                    String line;
                    while ((line = inputReader.readLine()) != null) {
                        if (line.equals(sign)) {
                            inputBarrier.await();
                        }
                    }
                } catch (IOException | InterruptedException | BrokenBarrierException e) {
                    log.error("输出流异常", e);
                }
            }
        });
        Thread errorSteam = new Thread(() -> {
            while (!shutdown) {
                try {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorBarrier.await();
                    }
                } catch (IOException | InterruptedException | BrokenBarrierException e) {
                    log.error("异常流异常", e);
                }
            }
        });
        inputSteam.start();
        errorSteam.start();
    }

    public boolean getState() throws InterruptedException {
        semaphore.acquire();
        semaphore.drainPermits();
        return state;
    }

    @Override
    public void close() throws IOException {
        shutdown = true;
        if (errorReader != null) {
            errorReader.close();
        }
        if (inputReader != null) {
            inputReader.close();
        }
    }

}
