package com.sam.demo.process1.exec;

import com.sam.demo.process1.AbstractProcessor;

import java.io.*;

public class TleProcessor extends AbstractProcessor {

    private RandomAccessFile raf;
    private RandomAccessFile bfr;

    public TleProcessor(String exeFullName) throws IOException {
        super(exeFullName);
        File tle = new File(home(), "TLE.txt");
        File kepl = new File(home(), "J2000KEPL.TXT");
        if (!tle.exists() || tle.isDirectory()) {
            tle.delete();
            tle.createNewFile();
        }
        if (!kepl.exists() || kepl.isDirectory()) {
            kepl.delete();
            kepl.createNewFile();
        }
        // 重要参数,完成信号
        sign("Stop - Program terminated.");
        raf = new RandomAccessFile(tle, "rw");
        bfr = new RandomAccessFile(kepl, "r");
    }

    @Override
    protected void next(String params) throws Exception {
        raf.setLength(0);
        raf.seek(0);
        raf.write(params.getBytes());
    }

    @Override
    protected void completed() throws Exception {
        raf.seek(0);
        String line = bfr.readLine();
        System.out.println(line);
    }

    @Override
    protected void error(String reason) throws Exception {
        System.err.println(reason);
    }

    public void close() throws IOException, InterruptedException {
        super.close();
        if (raf != null) {
            raf.close();
        }
        if (bfr != null) {
            bfr.close();
        }
    }

}
