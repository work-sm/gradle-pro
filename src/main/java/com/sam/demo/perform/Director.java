package com.sam.demo.perform;

import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.clock.Clock;
import com.sam.demo.perform.scene.Scene;
import com.sam.demo.perform.scene.impl.OnceScene;
import com.sam.demo.perform.scene.impl.RunnerScene;
import com.sam.demo.perform.script.Order;
import com.sam.demo.perform.script.Story;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author masx
 *
 * <p> 导演类: 管理演员，场景
 *
 * <p> 需要注意的是，两个构造所生成不同运行模式
 *
 * <p> hunger = true 饥饿模式，所有线程将没有次序的竞争资源使用权
 * <p> hunger = false 泳道模式，只允许 n 个线程并行，但这几个线程不会关闭，而是各自持有一个队列，相同泳道的排队执行
 * <p> 泳道以场景名区分唯一 {@link com.sam.demo.perform.script.Story}
 *
 * <p> 具体可参考 tle的调用 {@link com.sam.demo.perform.service.tle.Main}
 */
@Slf4j
public class Director {

    private final ThreadPoolExecutor pool;

    private final Map<String, Actor> actorTable = new HashMap<>();

    private final Map<String, Scene> sceneTable = new ConcurrentHashMap<>();

    private final Clock clock;

    private final boolean hunger;

    private final List<Story> stories = new ArrayList<>();

    public Director(int nThreads, Clock clock) {
        this.hunger = false;
        this.pool = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory());
        this.clock = clock;
    }

    public Director(Clock clock) {
        this.hunger = true;
        this.pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory());
        this.clock = clock;
    }

    public void register(Actor actor){
        actorTable.put(actor.name(), actor);
    }

    public void work(Story story) throws Exception {
        List<Order> actors = story.getStage();
        ready(actors);
        String sceneName = story.getScene();
        if(!hunger){
            if(StringUtils.isNotEmpty(sceneName) && sceneTable.containsKey(sceneName)){
                sceneTable.get(sceneName).story(story);
            }else{
                Scene scene = new RunnerScene(sceneName, clock, actorTable);
                scene.story(story);
                sceneTable.put(sceneName, scene);
                this.pool.execute(scene);
            }
        }else{
            Scene scene = new OnceScene(sceneName, clock, actorTable);
            scene.story(story);
            this.pool.execute(scene);
        }
        stories.add(story);
    }

    private void ready(List<Order> actors) throws Exception {
        for (Order order : actors) {
            if (!actorTable.containsKey(order.getActor())) {
                throw new Exception("actor not ready " + order.getActor());
            }
        }
    }

    public void destroy() throws Exception {
        Collection<Scene> scenes = sceneTable.values();
        for(Scene scene : scenes){
            scene.destroy();
        }
        pool.shutdown();
        while (!pool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
            log.info("await pool...");
        }
        for (Story story: stories){
            if(!story.isState()){
                log.info("异常抛出");
                Throwable throwable = story.getThrowable();
                throw new Exception(throwable);
            }
        }
        log.info("正常结束");
    }

}
