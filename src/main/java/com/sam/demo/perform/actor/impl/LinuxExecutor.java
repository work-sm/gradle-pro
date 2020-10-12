package com.hlht.mgt.perform.actor.impl;

import com.hlht.mgt.perform.actor.SingleActor;
import com.hlht.mgt.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@Slf4j
public class LinuxExecutor extends SingleActor {

    private String[] command;
    private String path;
    private String name;

    public LinuxExecutor(String[] command, String path, String name) {
        this.command = command;
        this.path = path;
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void visit(Story story) throws Exception {
        Process p = new ProcessBuilder(command)
                .directory(new File(path))
                .start();
        InputStream ins = p.getInputStream();
        InputStream ers = p.getErrorStream();
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(ins, Charset.forName("GBK")));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(ers, Charset.forName("GBK")));
            String line;
            while ((line = inputReader.readLine()) != null) {
                log.info("[in ]" + line);
            }
            while ((line = errReader.readLine()) != null) {
                log.error("[err ]" + line);
            }
            p.waitFor();
        } finally {
            ins.close();
            ers.close();
        }
    }

    @Override
    public void close() throws Exception {

    }

}
