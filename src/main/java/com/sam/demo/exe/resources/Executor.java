package com.sam.demo.exe.resources;

import com.sam.demo.exe.data.Carrier;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Executor<D extends Carrier> extends SingleResource<D> implements Resource<D> {

    private volatile boolean running = false;

    private Process process;
    private OutputStream outputStream;
    private InputStream inputStream;
    private InputStream errorStream;
    private BufferedReader inputBr;
    private BufferedReader errorBr;
    private BufferedWriter outputBw;
    private BufferedWriter logBw;

    private String exeName;
    private BlockingQueue<String> queue = new SynchronousQueue<>();

    private ThreadGroup threadGroup;

    private String sign = "EXE_IS_OK";
    private Semaphore lock = new Semaphore(0);

    public Executor(String path, String exeName) throws IOException {
        this.exeName = exeName;
        process = new ProcessBuilder("cmd")
                .directory(new File(path))
                .start();
        outputStream = process.getOutputStream();
        inputStream = process.getInputStream();
        errorStream = process.getErrorStream();
        inputBr = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
        errorBr = new BufferedReader(new InputStreamReader(errorStream, Charset.forName("GBK")));
        outputBw = new BufferedWriter(new OutputStreamWriter(outputStream));
        logBw = new BufferedWriter(new FileWriter(new File(path, "log.txt"), true));
        init();
    }

    private void init() {
        Runnable inputThread = () -> {
            String line;
            try {
                while ((line = inputBr.readLine()) != null) {
                    if(line.contains(sign)){
                        lock.release();
                    }
                    logBw.write("[in ] " + line);
                    logBw.write("\n");
                }
            } catch (IOException e) {
                log.error("input stream dead", e);
                try {
                    logBw.write("[ERR] INPUT STREAM DEAD");
                    logBw.write("\n");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        };

        Runnable errorThread = () -> {
            String line;
            try {
                while ((line = errorBr.readLine()) != null) {
                    logBw.write("[err] " + line);
                    logBw.write("\n");
                }
            } catch (IOException e) {
                log.error("error stream dead", e);
                try {
                    logBw.write("[ERR] ERROR STREAM DEAD");
                    logBw.write("\n");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        };

        Runnable outputThread = () -> {
            String line;
            try {
                while (running) {
                    try {
                        line = queue.poll(1000, TimeUnit.MILLISECONDS);
                        if(line == null) continue;
                    } catch (InterruptedException e) {
                        continue;
                    }
                    log.info("执行命令 {}", exeName);
                    outputBw.write("@echo off\n");
                    outputBw.write(line);
                    outputBw.write("\necho "+sign+"\n");
                    outputBw.flush();
                }
            } catch (IOException e) {
                log.error("output stream dead", e);
                try {
                    logBw.write("[ERR] OUTPUT STREAM DEAD");
                    logBw.write("\n");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        };

        running = true;
        threadGroup = new ThreadGroup("process_stream");
        Thread thread1 = new Thread(threadGroup, inputThread);
        thread1.setName("input_stream");
        Thread thread2 = new Thread(threadGroup, errorThread);
        thread2.setName("error_stream");
        Thread thread3 = new Thread(threadGroup, outputThread);
        thread3.setName("output_stream");
        thread1.start();
        thread2.start();
        thread3.start();
    }

    @Override
    public void doSomething(Carrier data) throws Exception {
        if(!running)
            throw new IllegalStateException("server is not running");
        log.info("写入命令 {}", exeName);
        queue.put(exeName);
        lock.acquire();
    }

    @Override
    public void close() {
        running = false;
        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(errorStream != null){
            try {
                errorStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        process.destroy();
        if(logBw!= null){
            try {
                logBw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        threadGroup.interrupt();
    }

}