package com.sam.demo.perform.script;

import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.actor.Action;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Story implements Script{

    private String unique;
    private String scene;
    private List<Order> stage = new LinkedList<>();

    @Override
    public void accept(Actor actor) throws Exception {
        actor.visit(this);
    }

    public void write(String name, Action action){
        Order build = Order.builder().actor(name).action(action).build();
        stage.add(build);
    }

}
