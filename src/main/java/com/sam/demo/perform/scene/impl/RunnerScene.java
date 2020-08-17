package com.sam.demo.perform.scene.impl;

import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.clock.Clock;
import com.sam.demo.perform.scene.Robot;
import com.sam.demo.perform.scene.Scene;
import com.sam.demo.perform.scene.StoryRobot;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RunnerScene implements Scene {

    private final String name;

    private volatile boolean running = true;

    private BlockingQueue<Story> queue = new LinkedBlockingQueue<>();

    private Semaphore lock = new Semaphore(0);

    private Robot<Story> robot;

    public RunnerScene(String name, Clock clock, Map<String, Actor> actors) {
        this.name = name;
        this.robot = new StoryRobot(actors, clock);
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
                robot.process(story);
            } catch (Exception e) {
                log.error("process error", e);
            }
        }
        // 释放后
        robot.close();
        lock.release();
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
