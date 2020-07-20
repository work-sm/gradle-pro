package com.sam.demo.process.exec;

import com.sam.demo.process.Processor;
import com.sam.demo.process.console.ReaderConsole;
import com.sam.demo.process.console.WriterConsole;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.*;

@Slf4j
public class TleProcessor implements Processor {

    private final static String EXE = "TLE_J2000KEPL.exe";

    private String binHome;

    private WriterConsole writerConsole;
    private ReaderConsole readerConsole;

    private TleResult tleResult;
    private TleParams tleParams;

    private volatile boolean shutdown;
    private Semaphore semaphore = new Semaphore(0);

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public TleProcessor(String binHome) throws Exception {
        this.binHome = binHome;
        this.shutdown = false;
        this.setup();
    }

    private void setup() throws Exception {
        File exe = new File(binHome, EXE);
        File tle = new File(binHome, "TLE.txt");
        File kepl = new File(binHome, "J2000KEPL.TXT");
        if (!exe.exists() || !exe.isFile()) {
            log.error("TLE_J2000KEPL.exe 不存在");
            throw new Exception("执行文件不存在");
        }
        if (!tle.exists() || tle.isDirectory()) {
            tle.delete();
            tle.createNewFile();
        }
        if (!kepl.exists() || kepl.isDirectory()) {
            kepl.delete();
            kepl.createNewFile();
        }

        Process process = Runtime.getRuntime().exec("cmd", null, new File(binHome));

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        writerConsole = new WriterConsole(outputStream);
        readerConsole = new ReaderConsole(inputStream, errorStream, "Stop - Program terminated.");

        tleResult = new TleResult(kepl);
        tleParams = new TleParams(tle);
    }

    @Override
    public void run() {
        String result = null;
        while (!this.shutdown) {
            String params = null;
            try {
                params = queue.poll(1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("处理器关闭");
                if(this.shutdown){
                    semaphore.release();
                }
            }
            try {
                tleParams.params(params);
                tleParams.waitFor();

                writerConsole.exec(EXE);
                boolean state = readerConsole.getState();
                if (state) {
                    result = tleResult.getResult();
                }
                log.info("===[{}]===[{}]===", params, result);
            } catch (InterruptedException | IOException | BrokenBarrierException e) {
                log.error("tle 处理过程异常", e);
            }
        }

        log.error("处理器关闭");
        if(this.shutdown){
            semaphore.release();
        }
    }

    @Override
    public boolean add(String param) {
        if(!this.shutdown){
            return queue.add(param);
        }
        return false;
    }

    @SneakyThrows
    @Override
    public void close() throws IOException {
        this.shutdown = true;
        semaphore.acquire();
        writerConsole.close();
        //不可靠
        readerConsole.close();
        // 后期优雅
        queue.clear();
    }

}
