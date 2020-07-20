package com.sam.demo.process1;

import com.sam.demo.process1.console.ReaderConsole;
import com.sam.demo.process1.console.WriterConsole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractProcessor implements Processor {

    private final String exe;
    private final File home;

    private WriterConsole writerConsole;
    private ReaderConsole readerConsole;

    private volatile boolean closed;
    private Semaphore semaphore = new Semaphore(0);
    private Semaphore paramsSign = new Semaphore(0);

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public AbstractProcessor(String exeFullName) throws IOException {
        if (StringUtils.isEmpty(exeFullName)) {
            throw new NullPointerException(exeFullName);
        }
        File file = new File(exeFullName);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException(exeFullName);
        }
        int l = exeFullName.lastIndexOf("\\");
        int i = exeFullName.lastIndexOf("/");
        int last = (l > i ? l : i) + 1;
        String binHome = exeFullName.substring(0, last);
        this.home = new File(binHome);
        this.exe = exeFullName.substring(last, exeFullName.length());

        Process process = Runtime.getRuntime().exec("cmd", null, this.home);

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        writerConsole = new WriterConsole(outputStream);
        readerConsole = new ReaderConsole(inputStream, errorStream);
    }

    public File home() {
        return home;
    }

    @Override
    public boolean queueUp(String param) {
        if (!this.closed) {
            return queue.add(param);
        }
        return false;
    }

    public void close() throws IOException, InterruptedException {
        this.closed = true;
        semaphore.acquire();
        writerConsole.close();
        readerConsole.close();
        queue.clear();
    }

    @Override
    public void run() {
        String result = null;
        while (!this.closed) {
            String params = null;
            try {
                params = queue.poll(1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                if (this.closed) {
                    log.info("处理器关闭");
                    semaphore.release();
                }
                continue;
            }
            try {
                try {
                    next(params);
                } finally {
                    semaphore.release();
                }
                semaphore.acquire();

                writerConsole.exec(exe);
                boolean state = readerConsole.getState();
                if (state) {
                    completed();
                } else {
                    error();
                }
                log.info("===[{}]===[{}]===", params, result);
            } catch (Exception e) {
                log.error("处理过程异常", e);
            }
        }

        if (this.closed) {
            log.info("处理器关闭");
            semaphore.release();
        }
    }

    protected abstract void next(String params) throws Exception;

    protected abstract void completed() throws Exception;

    protected abstract void error() throws Exception;

}
