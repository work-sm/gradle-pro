package com.sam.demo.exe.resources;

import com.sam.demo.exe.data.Carrier;

public interface Doers<D extends Carrier> {
    String name();
    void doSomething(D data) throws Exception;
}
