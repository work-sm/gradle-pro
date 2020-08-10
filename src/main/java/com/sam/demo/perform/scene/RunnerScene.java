package com.sam.demo.perform.scene;

import com.sam.demo.perform.actor.Action;
import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.clock.Clock;
import com.sam.demo.perform.script.Order;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RunnerScene implements Scene {

    private final String name;

    private volatile boolean running = true;

    private final BlockingQueue<Story> queue = new LinkedBlockingQueue<>();

    private Map<String, Actor> actorTable;

    private Semaphore lock = new Semaphore(0);

    private final Clock clock;

    public RunnerScene(String name, Clock clock) {
        this.name = name;
        this.clock = clock;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void run() {
        while (running) {
            Story story;
            try {
                try {
                    story = queue.poll(1000, TimeUnit.MILLISECONDS);
                    if (story == null) continue;
                } catch (InterruptedException e) {
                    continue;
                }

                log.info("场景 [{}] 处理 {}", name, story);
                process(story);
            } catch (Exception e) {
                log.error("process error", e);
            }
        }
        // 释放后
        close();
    }

    protected void process(Story story) throws Exception {
        List<Order> stage = story.getStage();
        for (Order step : stage) {
            story.stepping();
            Action action = step.getAction();
            String actor = step.getActor();
            if (Action.RELEASE == action) {
                log.info("actor [{}] RELEASE", actor);
                actorTable.get(actor).release();
            } else if (Action.INVITE == action) {
                log.info("actor [{}] INVITE", actor);
                try {
                    actorTable.get(actor).invite();
                    story.accept(actorTable.get(actor));
                } catch (Exception e) {
                    // 异常打断了循环体，手动释放,非优雅处理
                    releaseAll(stage, step);
                    clock.clock(story, "指令异常");
                    throw e;
                }
            }
            clock.clock(story);
        }
    }

    private void releaseAll(List<Order> stage, Order step) {
        int i = stage.indexOf(step);
        Set<String> locks = new HashSet<>();
        for (; i > -1; i--) {
            Order order = stage.get(i);
            Action action = order.getAction();
            String actor = order.getActor();
            if(Action.INVITE == action){
                locks.add(actor);
            }else if (Action.RELEASE == action) {
                locks.remove(actor);
            }
        }
        for(String lock:locks){
            log.info("异常释放 {}", lock);
            actorTable.get(lock).release();
        }
    }

    private void close() {
        Collection<Actor> actors = actorTable.values();
        actors.forEach(actor -> {
            try {
                actor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        lock.release();
    }

    @Override
    public void actors(Map<String, Actor> actors) {
        this.actorTable = actors;
    }

    @Override
    public void story(Story story) throws Exception {
        if (!running)
            throw new Exception("Scene is closed");
        queue.put(story);
        log.info("场景 [{}] 接受 {}", name, story);
    }

    @Override
    public void destroy() throws Exception {
        Story peek = queue.peek();
        while ((peek = queue.peek()) != null) {
            log.info("wait...");
            Thread.sleep(1000);
        }
        running = false;
        lock.acquire();
    }

}
