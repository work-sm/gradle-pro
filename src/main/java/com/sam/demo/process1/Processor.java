package com.sam.demo.process1;

public interface Processor extends Runnable {
    void produce(String param);
    void consume(ProductLine product);
}
