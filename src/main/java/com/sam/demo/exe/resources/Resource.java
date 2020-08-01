package com.sam.demo.exe.resources;

public interface Resource<D> {

    void control() throws InterruptedException;

    void doSomething(D data) throws Exception;

    void release();

    void close();

}
