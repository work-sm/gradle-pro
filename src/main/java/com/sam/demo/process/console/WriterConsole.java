package com.sam.demo.process.console;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Slf4j
public class WriterConsole implements Closeable {

    private CyclicBarrier barrier;

    private String command = "";
    private BufferedWriter bw;

    public WriterConsole(OutputStream os) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(os, Charset.forName("GBK"));
        bw = new BufferedWriter(osw);
        init();
    }

    public WriterConsole(OutputStream os, String runHome) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(os, Charset.forName("GBK"));
        bw = new BufferedWriter(osw);
        String drive = runHome.substring(0, 2);
        bw.write("@echo off\n");
        bw.write(drive + "\n");
        bw.write("cd " + runHome + "\n");
        bw.flush();
        init();
    }

    private void init() {
        barrier = new CyclicBarrier(1, () -> {
            try {
                bw.write(command + "\n");
                bw.flush();
            } catch (IOException e) {
                log.error("tle 命令流异常", e);
            }
        });
    }

    public void exec(String command) throws BrokenBarrierException, InterruptedException {
        this.command = command;
        barrier.await();
    }

    @Override
    public void close() throws IOException {
        if (bw != null) {
            bw.close();
        }
    }

}
