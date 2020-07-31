package com.sam.demo.process;

import com.sam.demo.process.console.ReaderConsole;
import com.sam.demo.process.console.WriterConsole;
import com.sam.demo.process.work.Element;
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

    private final int localNum;
    private final String exe;
    private final File home;

    private BlockingQueue<Element> queue;

    private WriterConsole writerConsole;
    private ReaderConsole readerConsole;

    private volatile boolean closed;
    private Semaphore lock;

    private ProductLine productLine;

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
        String num = binHome.substring(binHome.length() - 6, binHome.length() - 5);
        localNum = Integer.parseInt(num);

        Process process = Runtime.getRuntime().exec("cmd", null, this.home);

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        writerConsole = new WriterConsole(outputStream, exe, localNum);
        readerConsole = new ReaderConsole(inputStream, errorStream, exe, localNum);
        lock = new Semaphore(0);
        queue = new LinkedBlockingQueue<>();
    }

    protected File home() {
        return home;
    }

    protected void completeSigns(String sign) {
        readerConsole.completeSigns(sign);
    }

    protected void errorSigns(String sign) {
        readerConsole.errorSigns(sign);
    }

    protected String getReason() {
        return readerConsole.getReason();
    }

    @Override
    public void setFileBatch(String uuid){
        productLine.setFileBatch(uuid);
    }

    @Override
    public void consume(ProductLine productLine) {
        this.productLine = productLine;
    }

    @Override
    public void produce(Element element) {
        if (!this.closed) {
            queue.add(element);
        }
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
        Element element;
        readerConsole.start();
        writerConsole.start();
        while (!this.closed) {
            try {
                element = queue.poll(1000, TimeUnit.MILLISECONDS);
                if(element == null) continue;
            } catch (InterruptedException e) {
                if (this.closed) {
                    log.info("处理器关闭");
                    lock.release();
                }
                continue;
            }
            try {
                log.info("写入参数 [{}]", element);
                next(element);

                writerConsole.exec(exe);
                boolean state = readerConsole.getState(timeout());
                log.info("命令完成 [{}]", state);
                if (state) {
                    long waitTime = waitTime();
                    Thread.sleep(waitTime);
                    completed(element);
                } else {
                    error(element);
                }

                productLine.output(state, element);
            } catch (Exception e) {
                log.error("处理过程异常", e);
            }
        }

        if (this.closed) {
            log.info("处理器关闭");
            lock.release();
        }
    }

    protected abstract long waitTime();

    protected abstract long timeout();

    protected abstract void next(Element element) throws Exception;

    protected abstract void completed(Element element) throws Exception;

    protected abstract void error(Element element) throws Exception;

}
