package com.sam.demo.process.work;

import com.sam.demo.process.ProductLine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleProductLine implements ProductLine {
    @Override
    public void output(boolean state, Element element) {
        log.info("{} {}", state, element);
    }

    @Override
    public void setFileBatch(String uuid) {
    }
}
