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

    private final String name;
    private final Integer localNum;

    private CyclicBarrier inputBarrier;
    private CyclicBarrier errorBarrier;

    private BufferedReader inputReader;
    private BufferedReader errorReader;
    private List<String> completeSigns;
    private List<String> errorSigns;

    private volatile boolean state;
    private StringBuilder reason;
    private volatile boolean closed;
    private Semaphore lock;

    public ReaderConsole(InputStream input, InputStream error, String name, Integer localNum) {
        InputStreamReader inputStream = new InputStreamReader(input, Charset.forName("GBK"));
        inputReader = new BufferedReader(inputStream);
        InputStreamReader errorStream = new InputStreamReader(error, Charset.forName("GBK"));
        errorReader = new BufferedReader(errorStream);
        this.closed = false;
        this.completeSigns = new ArrayList<>();
        this.errorSigns = new ArrayList<>();
        this.lock = new Semaphore(0);
        this.name =  name.substring(0, name.length() -4);
        this.localNum = localNum;
        init();
    }

    private void init() {
        inputBarrier = new CyclicBarrier(1, () -> {
            log.info("命令完成");
            state = true;
            lock.release();
        });
        errorBarrier = new CyclicBarrier(1, () -> {
            log.info("命令异常");
            state = false;
            lock.release();
        });
    }

    public void start() {
        Thread inputSteam = new Thread(() -> {
            String line;
            String sign;
            Iterator<String> iterator;
            List<String> cTemps = new ArrayList<>();
            List<String> eTemps = new ArrayList<>();
            while (!closed) {
                try {
                    cTemps.addAll(completeSigns);
                    eTemps.addAll(errorSigns);
                    while ((line = inputReader.readLine()) != null) {
                        log.debug("==== [{}]", line);
                        if (completeSigns.isEmpty() && errorSigns.isEmpty()) {
                            if(errorSigns.isEmpty()){
                                inputReader.lines();
                                errorBarrier.await();
                                continue;
                            }else if(completeSigns.isEmpty()){
                                inputReader.lines();
                                inputBarrier.await();
                                continue;
                            }
                        }
                        iterator = cTemps.iterator();
                        while (iterator.hasNext()) {
                            sign = iterator.next();
                            if (line.contains(sign)) {
                                log.info("input 收到完成信号 {} [{}]", line, name);
                                iterator.remove();
                            }
                        }
                        iterator = eTemps.iterator();
                        while (iterator.hasNext()) {
                            sign = iterator.next();
                            if (line.contains(sign)) {
                                log.info("input 收到异常信号 {} [{}]", line, name);
                                reason = new StringBuilder();
                                reason.append(line).append("\n");
                                iterator.remove();
                            }
                        }
                        if (cTemps.size() == 0 || eTemps.size() == 0) {
                            if(eTemps.size() == 0){
                                log.info("input [{}] 异常信号完整", name);
                                cTemps.clear();
                                cTemps.addAll(completeSigns);
                                eTemps.clear();
                                eTemps.addAll(errorSigns);
                                inputReader.lines();
                                errorBarrier.await();
                            }else if(cTemps.size() == 0){
                                log.info("input [{}] 完成信号完整", name);
                                cTemps.clear();
                                cTemps.addAll(completeSigns);
                                eTemps.clear();
                                eTemps.addAll(errorSigns);
                                inputReader.lines();
                                inputBarrier.await();
                            }
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
                        log.info("error 收到异常信号 [{}]", reason.toString());
                        errorBarrier.await();
                    }
                } catch (IOException | InterruptedException | BrokenBarrierException e) {
                    log.error("异常流异常", e);
                }
            }
        });
        inputSteam.setName(name + "_I_reader"+localNum);
        errorSteam.setName(name + "_E_reader"+localNum);
        inputSteam.start();
        errorSteam.start();
    }

    public void completeSigns(String sign) {
        completeSigns.add(sign);
    }

    public void errorSigns(String sign) {
        errorSigns.add(sign);
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
        if(reason == null) return "read time out err.";
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
