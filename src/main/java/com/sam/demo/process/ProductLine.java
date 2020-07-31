package com.sam.demo.process;

import com.sam.demo.process.work.Element;

public interface ProductLine {
    void output(boolean state, Element element);
    void setFileBatch(String uuid);
}
