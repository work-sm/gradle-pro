package com.sam.demo.perform.scene;

import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.script.Story;

import java.util.Map;

public interface Scene extends Runnable {

    String name();

    void actors(Map<String, Actor> actors);

    void story(Story story) throws Exception;

    void destroy() throws Exception;

}
