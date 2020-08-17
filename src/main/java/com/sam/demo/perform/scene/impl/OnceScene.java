package com.sam.demo.perform.scene.impl;

import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.clock.Clock;
import com.sam.demo.perform.scene.Robot;
import com.sam.demo.perform.scene.Scene;
import com.sam.demo.perform.scene.StoryRobot;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class OnceScene implements Scene {

    private final String name;

    private Story story;

    private Robot<Story> robot;

    public OnceScene(String name, Clock clock, Map<String, Actor> actors) {
        this.name = name;
        this.robot = new StoryRobot(actors, clock);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void story(Story story) throws Exception {
        this.story = story;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void run() {
        try {
            log.info("场景 [{}] 处理 {}", name, story);
            robot.process(story);
        } catch (Exception e) {
            log.error("process error", e);
        }
    }

}
