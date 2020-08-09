package com.sam.demo.perform.actor.impl;

import com.sam.demo.perform.script.Story;
import com.sam.demo.perform.actor.SingleActor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class NopActor extends SingleActor {

    private final String name;

    public NopActor(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void visit(Story story) throws Exception {
        log.info("{} -> story {}", name, story);
    }

    @Override
    public void close() throws Exception {

    }

}
