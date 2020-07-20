package com.sam.demo.process1.exec;

import com.sam.demo.process1.AbstractProcessor;

import java.io.*;

public class TleProcessor extends AbstractProcessor {

    private File tle;
    private File kepl;

    public TleProcessor(String exeFullName) throws IOException {
        super(exeFullName);
        tle = new File(home(), "TLE.txt");
        kepl = new File(home(), "J2000KEPL.TXT");
        if (!tle.exists() || tle.isDirectory()) {
            tle.delete();
            tle.createNewFile();
        }
        if (!kepl.exists() || kepl.isDirectory()) {
            kepl.delete();
            kepl.createNewFile();
        }
    }

    @Override
    protected void next(String params) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(tle, "rw")) {
            raf.setLength(0);
            raf.seek(0);
            raf.write(params.getBytes());
        }
    }

    @Override
    protected void completed() throws Exception {
        try (BufferedReader bfr = new BufferedReader(new FileReader(kepl))) {
            String line = bfr.readLine();
            System.out.println(line);
        }
    }

    @Override
    protected void error() throws Exception {
        System.err.println("异常");
    }

}
