package com.sam.demo.exe.impl;

import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.resources.Resource;
import com.sam.demo.exe.resources.SingleResource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@Slf4j
public class Writer extends SingleResource<Carrier> implements Resource<Carrier> {

    private RandomAccessFile raf;

    public Writer(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        raf = new RandomAccessFile(file, "rw");
    }

    @Override
    public void doSomething(Carrier data) throws Exception {
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
