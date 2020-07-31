package com.sam.demo.process;

import com.sam.demo.process.work.Element;

public interface Processor extends Runnable {
    void produce(Element element);
    void consume(ProductLine product);
    void setFileBatch(String uuid);
}
