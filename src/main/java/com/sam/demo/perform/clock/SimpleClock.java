package com.sam.demo.perform.clock;

import com.sam.demo.perform.script.Order;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String division = division(index, total);
        String desc = order.getDesc();
        log.info("[Clock] [{}] {} [{}]", unique, division, order.getDesc());
    }

    @Override
    public void clock(Story story, String msg) {
        String unique = story.getUnique();
        List<Order> stage = story.getStage();
        int total = story.getTotal().get();
        int index = story.getIndex().get();
        Order order = stage.get(index - 1);
        String division = division(index, total);
        log.error("[Clock] [{}] {} [{}] err {}", unique, division, order.getDesc(), msg);
    }

    public String division(int num1, int num2) {
        String rate = "0.00";
        String format = rate;
        if (num2 > 0 && num1 != 0) {
            DecimalFormat dec = new DecimalFormat(format);
            rate = dec.format((double) num1 / num2 * 100);
        }
        return rate;
    }


}
