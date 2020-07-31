package com.sam.demo.process.console;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

@Slf4j
public class WriterConsole implements Closeable {

    private Semaphore semaphore = new Semaphore(0);

    private final String name;
    private final Integer localNum;
    private String command = "";
    private BufferedWriter bw;

    private volatile boolean closed;

    public WriterConsole(OutputStream os, String name, Integer localNum) {
        OutputStreamWriter osw = new OutputStreamWriter(os, Charset.forName("GBK"));
        bw = new BufferedWriter(osw);
        closed = false;
        this.name = name.substring(0, name.length() -4);
        this.localNum = localNum;
    }

    public WriterConsole(OutputStream os, String runHome, String name, Integer localNum) throws IOException {
        this(os, name, localNum);
        String drive = runHome.substring(0, 2);
        bw.write("@echo off\n");
        bw.write(drive + "\n");
        bw.write("cd " + runHome + "\n");
        bw.flush();
    }

    public void start() {
        Runnable runnable = () -> {
            while (!closed) {
                try {
                    semaphore.acquire();
                    bw.write(command + "\n");
                    log.info("发出命令 [{}]", command);
                    bw.flush();
                } catch (InterruptedException e) {
                    log.error("等待异常", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        log.error("等待异常", e1);
                    }
                } catch (IOException e) {
                    log.error("发送命令流异常", e);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.setName(name+"_writer"+localNum);
        thread.start();
    }

    public void exec(String command) {
        if (StringUtils.isEmpty(command)) {
            throw new NullPointerException(command);
        }
        this.command = command;
        semaphore.release();
    }

    @Override
    public void close() throws IOException {
        closed = true;
        if (bw != null) {
            bw.close();
        }
    }

}
