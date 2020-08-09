package com.sam.demo.perform.script;

import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.actor.Action;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@ToString
public class Story implements Script{

    private String unique;
    private String scene;
    private List<Order> stage = new LinkedList<>();
    private AtomicInteger total = new AtomicInteger();
    private AtomicInteger index = new AtomicInteger();

    @Override
    public void stepping() {
        index.getAndIncrement();
    }

    @Override
    public void accept(Actor actor) throws Exception {
        actor.visit(this);
    }

    public void write(String name, Action action){
        write(name, action, "");
    }

    public void write(String name, Action action, String desc){
        Order build = Order.builder().actor(name).action(action).desc(desc).build();
        stage.add(build);
        total.getAndIncrement();
    }

}
