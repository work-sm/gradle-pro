package com.sam.demo.perform.clock;

import com.sam.demo.perform.script.Order;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SimpleClock implements Clock {

    @Override
    public void clock(Story story) {
        // only read
        String unique = story.getUnique();
        List<Order> stage = story.getStage();
        int total = story.getTotal().get();
        int index = story.getIndex().get();
        Order order = stage.get(index - 1);
        log.info("[Clock] [{}] {}/{} [{}]", unique, index, total, order.getDesc());
    }

}
