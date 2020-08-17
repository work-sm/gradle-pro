package com.sam.demo.perform.scene;

import com.sam.demo.perform.script.Story;

/**
 * @author masx
 *
 * <p> 场景的意义是封装线程，让故事可以发生在线程内
 * <p> 现阶段有两种 实现，分别为饥饿模式
 * <p> 模式可以参阅 {@link com.sam.demo.perform.Director}
 *
 * <p> 饥饿模式 {@link com.sam.demo.perform.scene.impl.OnceScene}
 * <p> 泳道模式 {@link com.sam.demo.perform.scene.impl.RunnerScene}
 */
public interface Scene extends Runnable {

    /**
     * 场景唯一名，如果重复，在饥饿模式下没事，在泳道模式下会和已存在的泳道（队列）下排队
     * 模式可以参阅 {@link com.sam.demo.perform.Director}
     * @return
     */
    String name();

    /**
     * 这里只是给场景设置故事，并未执行，执行应该在 {@link Runnable#run()}
     * @param story
     * @throws Exception
     */
    void story(Story story) throws Exception;

    /**
     * 结束时释放资源
     * @throws Exception
     */
    void destroy() throws Exception;

}
