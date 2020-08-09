package com.sam.demo.perform.actor;

import com.sam.demo.perform.script.Story;

public interface Actor {

    String name();

    void visit(Story story) throws Exception;

    void invite() throws Exception;

    void release() throws Exception;

    void close() throws Exception;

}
