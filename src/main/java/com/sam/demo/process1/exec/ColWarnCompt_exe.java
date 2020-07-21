package com.sam.demo.process1.exec;

import com.sam.demo.process1.AbstractProcessor;
import com.sam.demo.process1.util.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ColWarnCompt_exe extends AbstractProcessor {

    private RandomAccessFile raf;
    private RandomAccessFile bfr;

    public ColWarnCompt_exe(String exeFullName) throws IOException {
        super(exeFullName);
        File paramHome = new File(home().getParent(), "data\\inputData");
        File resultHome = new File(home().getParent(), "data\\outData");
        File tle = new File(paramHome, "satPara_normal.json");
        String name = JsonUtils.getVal(tle, "$.name");
        File kepl = new File(resultHome, "ascNodeData_" + name + ".txt");
        if (!tle.exists() || tle.isDirectory()) {
            tle.delete();
            tle.createNewFile();
        }
        if (!kepl.exists() || kepl.isDirectory()) {
            kepl.delete();
            kepl.createNewFile();
        }
        sign("CALCULATION COMPLETED");
        raf = new RandomAccessFile(tle, "rw");
        bfr = new RandomAccessFile(kepl, "r");
    }

    @Override
    protected long waitTime() {
        return 0;
    }

    @Override
    protected void next(String params) throws Exception {

    }

    @Override
    protected String completed() throws Exception {
        return null;
    }
}
