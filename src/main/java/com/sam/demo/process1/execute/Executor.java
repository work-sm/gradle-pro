package com.sam.demo.process1.execute;

import com.sam.demo.process1.Processor;
import com.sam.demo.process1.work.Element;

public interface Executor {
    void init(Integer id, String name, Processor[] processors);
    void execute(Integer id, Element element, String uuid) throws Exception;
}
