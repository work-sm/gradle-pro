package com.sam.demo.exe.impl;

import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.resources.Doers;
import com.sam.demo.exe.resources.SingleResource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@Slf4j
public class Writer extends SingleResource implements Doers<Carrier> {

    private RandomAccessFile raf;

    public Writer(String filePath) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
        raf = new RandomAccessFile(file, "rw");
    }

    @Override
    public void doSomething(Carrier data) throws Exception {
        log.info("刷新参数");
        raf.setLength(0);
        raf.seek(0);
        raf.write(data.getWriterContent().getBytes());
    }

    @Override
    public void close() {
        if (raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
