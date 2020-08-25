package com.sam.demo.perform.script;

import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.actor.Action;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author masx
 *
 * <p> 故事类：
 * <p> 成员 unique 故事唯一id
 * <p> 成员 scene 故事发生场景（此属性与{@link com.sam.demo.perform.Director#work(Story)}） 的工作模式有关
 * <p> 成员 total 重点成员，故事总共多少步
 * <p> 成员 index 重点成员，进行到哪一步
 * <p> 成员 stage 重点成员，步伐队列 {@link com.sam.demo.perform.script.Order}
 */
@Getter
@Setter
@ToString
public class Story implements Script{

    private String unique;
    private String scene;
    private boolean state = true;
    private LinkedList<Order> stage = new LinkedList<>();
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
        Order build = Order.builder()
                .actor(name)
                .action(action)
                .desc(desc)
                .build();
        stage.add(build);
        total.getAndIncrement();
    }

}
