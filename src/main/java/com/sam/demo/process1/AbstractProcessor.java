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

    private BlockingQueue<String> queue;

    private WriterConsole writerConsole;
    private ReaderConsole readerConsole;

    private volatile boolean closed;
    private Semaphore lock;

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
        lock = new Semaphore(0);
        queue = new LinkedBlockingQueue<>();
    }

    public File home() {
        return home;
    }

    public void sign(String sign) {
        readerConsole.sign(sign);
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
        lock.acquire();
        writerConsole.close();
        readerConsole.close();
        queue.clear();
    }

    @Override
    public void run() {
        String reason;
        String params;
        readerConsole.start();
        writerConsole.start();
        while (!this.closed) {
            try {
                params = queue.poll(1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                if (this.closed) {
                    log.info("处理器关闭");
                    lock.release();
                }
                continue;
            }
            try {
                next(params);
                log.info("写入参数 [{}]", params);

                writerConsole.exec(exe);
                boolean state = readerConsole.getState();
                log.info("命令完成 [{}]", state);
                if (state) {
                    completed();
                } else {
                    reason = readerConsole.getReason();
                    error(reason);
                }
            } catch (Exception e) {
                log.error("处理过程异常", e);
            }
        }

        if (this.closed) {
            log.info("处理器关闭");
            lock.release();
        }
    }

    protected abstract void next(String params) throws Exception;

    protected abstract void completed() throws Exception;

    protected abstract void error(String reason) throws Exception;

}
