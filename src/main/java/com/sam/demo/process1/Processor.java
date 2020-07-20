package com.sam.demo.process1;

public interface Processor extends Runnable {
    boolean queueUp(String param);
}
