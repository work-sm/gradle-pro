package com.sam.demo.exe.impl;

import com.sam.demo.exe.impl.data.Carrier;
import com.sam.demo.exe.resources.Resource;
import com.sam.demo.exe.resources.SingleResource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Reader extends SingleResource<Carrier> implements Resource<Carrier> {

    @Override
    public void doSomething(Carrier data) throws Exception {
        String name = Thread.currentThread().getName();
        long id = Thread.currentThread().getId();
        data.setName(data.getName() + name + id + "Reader");
    }

    @Override
    public void close() {

    }

}
