package com.sam.demo.process1.console;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

@Slf4j
public class WriterConsole implements Closeable {

    private Semaphore semaphore = new Semaphore(0);

    private String command = "";
    private BufferedWriter bw;

    private volatile boolean closed;

    public WriterConsole(OutputStream os) {
        OutputStreamWriter osw = new OutputStreamWriter(os, Charset.forName("GBK"));
        bw = new BufferedWriter(osw);
        closed = false;
    }

    public WriterConsole(OutputStream os, String runHome) throws IOException {
        this(os);
        String drive = runHome.substring(0, 2);
        bw.write("@echo off\n");
        bw.write(drive + "\n");
        bw.write("cd " + runHome + "\n");
        bw.flush();
    }

    private void start() {
        Runnable runnable = () -> {
            while (!closed) {
                try {
                    semaphore.acquire();
                    bw.write(command + "\n");
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
        new Thread(runnable).start();
    }

    public void exec(String command) {
        if(StringUtils.isEmpty(command)){
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
