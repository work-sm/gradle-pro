package com.sam.demo.perform.service.tle;

import com.sam.demo.perform.actor.impl.NopActor;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@Slf4j
public class Writer extends NopActor {

    private RandomAccessFile raf;

    public Writer(String filePath, String name) throws IOException {
        super(name);
        File file = new File(filePath);
        file.createNewFile();
        raf = new RandomAccessFile(file, "rw");
    }

    @Override
    public void visit(Story story) throws IOException {
        TleStory tleStory = (TleStory) story;
        log.info("刷新参数");
        raf.setLength(0);
        raf.seek(0);
        raf.write(tleStory.getParam().getBytes());
    }

    @Override
    public void close() throws Exception {
        if (raf != null) {
            raf.close();
        }
    }
}
