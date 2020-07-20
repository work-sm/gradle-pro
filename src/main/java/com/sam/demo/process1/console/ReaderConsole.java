package com.sam.demo.process1.console;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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
    private List<String> signs;

    private boolean state;
    private volatile boolean closed;

    public ReaderConsole(InputStream input, InputStream error) {
        InputStreamReader inputStream = new InputStreamReader(input, Charset.forName("GBK"));
        inputReader = new BufferedReader(inputStream);
        InputStreamReader errorStream = new InputStreamReader(error, Charset.forName("GBK"));
        errorReader = new BufferedReader(errorStream);
        this.closed = false;
        this.signs = new ArrayList<>();
        init();
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

    private void start() {
        Thread inputSteam = new Thread(() -> {
            List<String> temps = new ArrayList<>();
            while (!closed) {
                try {
                    temps.addAll(signs);
                    String line;
                    while ((line = inputReader.readLine()) != null) {
                        for (String sign : temps) {
                            if (line.contains(sign)) {
                                temps.remove(sign);
                            }
                        }
                        if (temps.size() == 0) {
                            inputReader.lines();
                            inputBarrier.await();
                        }
                    }
                } catch (IOException | InterruptedException | BrokenBarrierException e) {
                    log.error("输出流异常", e);
                }
            }
        });
        Thread errorSteam = new Thread(() -> {
            while (!closed) {
                try {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        inputReader.lines();
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

    public void sign(String sign) {
        signs.add(sign);
    }

    public boolean getState() throws InterruptedException {
        semaphore.acquire();
        semaphore.drainPermits();
        return state;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        if (errorReader != null) {
            errorReader.close();
        }
        // 输入流关不掉
        if (inputReader != null) {
            inputReader.close();
        }
    }

}
