package com.sam.demo.perform.actor;

import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class Executor extends SingleActor {

    private final String name;

    private volatile boolean running = false;

    private Process process;
    private OutputStream outputStream;
    private InputStream inputStream;
    private InputStream errorStream;
    private BufferedReader inputBr;
    private BufferedReader errorBr;
    private BufferedWriter outputBw;

    private CommandHandler handler = new NopHandler();

    private BlockingQueue<String> queue = new SynchronousQueue<>();

    private ThreadGroup threadGroup;

    private String sign = "EXE_IS_OK";
    private Semaphore lock = new Semaphore(0);

    public Executor(String path, String name) throws IOException {
        this.name = name;
        process = new ProcessBuilder("cmd")
                .directory(new File(path))
                .start();
        outputStream = process.getOutputStream();
        inputStream = process.getInputStream();
        errorStream = process.getErrorStream();
        inputBr = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
        errorBr = new BufferedReader(new InputStreamReader(errorStream, Charset.forName("GBK")));
        outputBw = new BufferedWriter(new OutputStreamWriter(outputStream));
        init();
    }

    private void init() {
        Runnable inputThread = () -> {
            String line;
            try {
                while ((line = inputBr.readLine()) != null) {
                    if (line.contains(sign)) {
                        lock.release();
                        log.info("命令完成 [{}]", line);
                    }
                }
            } catch (IOException e) {
                log.error(name + " input stream dead", e);
            }
        };

        Runnable errorThread = () -> {
            String line;
            try {
                while ((line = errorBr.readLine()) != null) {
                    log.error("错误命令 [{}]", line);
                }
            } catch (IOException e) {
                log.error(name + " error stream dead", e);
            }
        };

        Runnable outputThread = () -> {
            String line;
            try {
                while (running) {
                    try {
                        line = queue.poll(1000, TimeUnit.MILLISECONDS);
                        if (line == null) continue;
                    } catch (InterruptedException e) {
                        continue;
                    }
                    log.info("执行命令 {}", line);
                    line = handler.handle(line);
                    outputBw.write("@echo off\n" + line + "\necho " + sign + "\n");
                    outputBw.flush();
                }
            } catch (IOException e) {
                log.error(name + " output stream dead", e);
            }
        };

        running = true;
        threadGroup = new ThreadGroup(name + "stream_group");
        Thread thread1 = new Thread(threadGroup, inputThread);
        thread1.setName(name + "_input_stream");
        Thread thread2 = new Thread(threadGroup, errorThread);
        thread2.setName(name + "_error_stream");
        Thread thread3 = new Thread(threadGroup, outputThread);
        thread3.setName(name + "_output_stream");
        thread1.start();
        thread2.start();
        thread3.start();
    }

    @Override
    public String name() {
        return this.name;
    }

    public void setHandler(CommandHandler handler){
        this.handler = handler;
    }

    @Override
    public void visit(Story story) throws Exception {
        if(!running)
            throw new IllegalStateException("server is not running");
        queue.put(name);
        lock.acquire();
        // 收到信号，并未刷新返回文件
        strategy();
    }

    protected abstract void strategy() throws Exception;

    @Override
    public void close() throws Exception {
        running = false;
        process.destroy();
        threadGroup.interrupt();
        if (outputStream != null) {
            outputStream.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

}