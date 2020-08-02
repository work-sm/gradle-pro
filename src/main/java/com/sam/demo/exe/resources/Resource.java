package com.sam.demo.exe.resources;

import com.sam.demo.exe.data.Carrier;

public interface Resource<D extends Carrier> {

    void control() throws InterruptedException;

    void doSomething(D data) throws Exception;

    void release();

    void close();

}
