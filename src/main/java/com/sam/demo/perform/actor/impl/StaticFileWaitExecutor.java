package com.sam.demo.perform.actor.impl;

import cn.hutool.core.io.FileUtil;
import com.sam.demo.perform.actor.Executor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

public class StaticFileWaitExecutor extends Executor {

    private final int millis;
    private final File staticFile;

    public StaticFileWaitExecutor(String path, String name) throws IOException {
        this(path, name, 1000);
    }

    public StaticFileWaitExecutor(String path, String name, int millis) throws IOException {
        super(path, name);
        this.millis = millis;
        this.staticFile = new File(new File(path).getParent(), "/POD/ODTKS/consoleOut.txt");
        FileUtil.del(staticFile);
    }

    @Override
    protected void strategy() throws Exception {
        while (!staticFile.exists()) {
            Thread.sleep(millis);
        }
        try (RandomAccessFile input = new RandomAccessFile(staticFile, "r")){
            String s = FileUtil.readLine(input, Charset.forName("UTF-8"));
            if (Integer.parseInt(s) < 0) {
                throw new Exception("运行异常，请查看日志");
            }
        } finally {
            FileUtil.del(staticFile);
        }
    }

}
