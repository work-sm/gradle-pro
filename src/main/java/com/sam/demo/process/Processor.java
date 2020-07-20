package com.sam.demo.process;

import java.io.Closeable;

public interface Processor extends Runnable ,Closeable {
    boolean add(String param);
}
