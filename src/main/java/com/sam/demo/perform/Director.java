package com.sam.demo.perform;

import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.scene.RunnerScene;
import com.sam.demo.perform.scene.Scene;
import com.sam.demo.perform.script.Order;
import com.sam.demo.perform.script.Story;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.*;

public class Director {

    private final ThreadPoolExecutor pool;

    private final Map<String, Actor> actorTable = new HashMap<>();

    private final Map<String, Scene> sceneTable = new ConcurrentHashMap<>();

    public Director(int nThreads) {
        this.pool = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory());
    }

    public void register(Actor actor){
        actorTable.put(actor.name(), actor);
    }

    public void work(Story story) throws Exception {
        List<Order> actors = story.getStage();
        ready(actors);
        String sceneName = story.getScene();
        if(StringUtils.isNotEmpty(sceneName) && sceneTable.containsKey(sceneName)){
            sceneTable.get(sceneName).story(story);
        }else{
            Scene scene = new RunnerScene(sceneName);
            scene.actors(actorTable);
            scene.story(story);
            sceneTable.put(sceneName, scene);
            this.pool.execute(scene);
        }
    }

    public void destroy() throws Exception {
        Collection<Scene> scenes = sceneTable.values();
        for(Scene scene : scenes){
            scene.destroy();
        }
        pool.shutdown();
    }

    private void ready(List<Order> actors) throws Exception {
        for (Order order : actors) {
            if (!actorTable.containsKey(order.getActor())) {
                throw new Exception("actor not ready " + order.getActor());
            }
        }
    }

}
