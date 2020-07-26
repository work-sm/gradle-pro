package com.sam.demo.process1.work;

import com.sam.demo.process1.ProductLine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleProductLine implements ProductLine {
    @Override
    public void output(boolean state, Element element) {
        log.info("{} {}", state, element);
    }
}
