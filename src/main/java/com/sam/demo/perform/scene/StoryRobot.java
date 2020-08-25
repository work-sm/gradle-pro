package com.sam.demo.perform.scene;

import com.sam.demo.perform.actor.Action;
import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.clock.Clock;
import com.sam.demo.perform.script.Order;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class StoryRobot implements Robot<Story> {

    private Map<String, Actor> actorTable;

    private Clock clock;

    public StoryRobot(Map<String, Actor> actors, Clock clock) {
        this.actorTable = actors;
        this.clock = clock;
    }

    @Override
    public void process(Story story) throws Exception {
        LinkedList<Order> stage = story.getStage();
        for (Order step : stage) {
            story.stepping();
            Action action = step.getAction();
            String actor = step.getActor();
            clock.clock(story);
            if (Action.RELEASE == action) {
                log.info("actor [{}] RELEASE", actor);
                actorTable.get(actor).release();
            } else if (Action.INVITE == action) {
                log.info("actor [{}] INVITE", actor);
                try {
                    actorTable.get(actor).invite();
                    story.accept(actorTable.get(actor));
                } catch (Exception e) {
                    story.setState(false);
                    // 异常打断了循环体，手动释放,非优雅处理
                    releaseAll(stage, step);
                    clock.clock(story, "指令异常");
                    throw e;
                }
            }
        }
    }

    private void releaseAll(LinkedList<Order> stage, Order step) {
        int i = stage.indexOf(step);
        Set<String> locks = new HashSet<>();
        for (; i > -1; i--) {
            Order order = stage.get(i);
            Action action = order.getAction();
            String actor = order.getActor();
            if (Action.INVITE == action) {
                locks.add(actor);
            } else if (Action.RELEASE == action) {
                locks.remove(actor);
            }
        }
        for (String lock : locks) {
            log.info("异常释放 {}", lock);
            actorTable.get(lock).release();
        }
    }

    @Override
    public void close() {
        Collection<Actor> actors = actorTable.values();
        actors.forEach(actor -> {
            try {
                actor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
