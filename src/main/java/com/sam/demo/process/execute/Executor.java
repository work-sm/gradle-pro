package com.sam.demo.process.execute;

import com.sam.demo.process.Processor;
import com.sam.demo.process.work.Element;

public interface Executor {
    void init(Integer id, String name, Processor[] processors);
    void execute(Integer id, Element element, String uuid) throws Exception;
}
