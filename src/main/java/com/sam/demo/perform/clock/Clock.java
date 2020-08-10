package com.sam.demo.perform.clock;

import com.sam.demo.perform.script.Story;

public interface Clock {
    void clock(Story story);
    void clock(Story story, String msg);
}
