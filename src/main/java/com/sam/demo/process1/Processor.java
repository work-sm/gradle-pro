package com.sam.demo.process1;

import com.sam.demo.process1.work.Element;

public interface Processor extends Runnable {
    void produce(Element element);
    void consume(ProductLine product);
}
