package com.sam.demo.perform.service.tle;

import com.sam.demo.perform.actor.impl.NopActor;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@Slf4j
public class Reader extends NopActor {

    private File file;
    private RandomAccessFile bfr;

    public Reader(String filePath, String name) throws IOException {
        super(name);
        file = new File(filePath);
        file.createNewFile();
        bfr = new RandomAccessFile(file, "r");
    }

    @Override
    public void visit(Story story) throws Exception {
        log.info("获取结果");
        TleStory tleStory = (TleStory) story;
        bfr.seek(0);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bfr.readLine())!= null){
            sb.append(line).append("\n");
        }
        tleStory.setResult(sb.toString());
        file.delete();
    }

    @Override
    public void close() throws Exception {
        if (bfr != null) {
            bfr.close();
        }
    }
}
