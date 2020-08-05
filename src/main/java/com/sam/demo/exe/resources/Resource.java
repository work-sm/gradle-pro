package com.sam.demo.exe.resources;

public interface Resource {

    void control() throws InterruptedException;

    void release();

    void close();

}
