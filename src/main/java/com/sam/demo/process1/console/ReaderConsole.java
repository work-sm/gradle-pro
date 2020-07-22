package com.sam.demo.process1.console;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class ReaderConsole implements Closeable {

    private CyclicBarrier inputBarrier;
    private CyclicBarrier errorBarrier;

    private BufferedReader inputReader;
    private BufferedReader errorReader;
    private List<String> signs;

    private volatile boolean state;
    private StringBuilder reason;
    private volatile boolean closed;
    private Semaphore lock;

    public ReaderConsole(InputStream input, InputStream error) {
        InputStreamReader inputStream = new InputStreamReader(input, Charset.forName("GBK"));
        inputReader = new BufferedReader(inputStream);
        InputStreamReader errorStream = new InputStreamReader(error, Charset.forName("GBK"));
        errorReader = new BufferedReader(errorStream);
        this.closed = false;
        this.signs = new ArrayList<>();
        this.lock = new Semaphore(0);
        init();
    }

    private void init() {
        inputBarrier = new CyclicBarrier(1, () -> {
            log.debug("命令完成");
            state = true;
            lock.release();
        });
        errorBarrier = new CyclicBarrier(1, () -> {
            log.debug("命令异常");
            state = false;
            lock.release();
        });
    }

    public void start() {
        Thread inputSteam = new Thread(() -> {
            String line;
            String sign;
            Iterator<String> iterator;
            List<String> temps = new ArrayList<>();
            while (!closed) {
                try {
                    temps.addAll(signs);
                    while ((line = inputReader.readLine()) != null) {
                        log.debug("==== [{}]", line);
                        if (signs.isEmpty()) {
                            inputReader.lines();
                            inputBarrier.await();
                            continue;
                        }
                        iterator = temps.iterator();
                        while (iterator.hasNext()) {
                            sign = iterator.next();
                            if (line.contains(sign)) {
                                log.debug("收到命令信号 [{}]", line);
                                iterator.remove();
                            }
                        }
                        if (temps.size() == 0) {
                            log.debug("收到信号完整");
                            temps.clear();
                            temps.addAll(signs);
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
            String line;
            Stream<String> lines;
            while (!closed) {
                try {
                    while ((line = errorReader.readLine()) != null) {
                        lines = errorReader.lines();
                        reason = new StringBuilder();
                        reason.append(line).append("\n");
                        lines.forEach(lin -> {
                            reason.append(lin).append("\n");
                        });
                        log.debug("收到异常信号 [{}]", reason.toString());
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

    public boolean getState(long millis) throws InterruptedException {
        if(millis > 0){
            lock.tryAcquire(millis, TimeUnit.MILLISECONDS);
        }else{
            lock.acquire();
        }
        lock.drainPermits();
        return state;
    }

    public String getReason() {
        return reason.toString();
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
