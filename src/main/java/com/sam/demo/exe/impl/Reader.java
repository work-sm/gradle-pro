package com.sam.demo.exe.impl;

import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.resources.Resource;
import com.sam.demo.exe.resources.SingleResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@Slf4j
public class Reader extends SingleResource<Carrier> implements Resource<Carrier> {

    private File file;
    private RandomAccessFile bfr;

    public Reader(String filePath) throws IOException {
        file = new File(filePath);
        file.createNewFile();
        bfr = new RandomAccessFile(file, "r");
    }

    @Override
    public void doSomething(Carrier data) throws Exception {
        bfr.seek(0);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bfr.readLine())!= null){
            sb.append(line).append("\n");
        }
        data.setReadContent(sb.toString());
        FileUtils.forceDeleteOnExit(file);
    }

    @Override
    public void close() {
        if (bfr != null) {
            try {
                bfr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
