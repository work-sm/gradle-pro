package com.sam.demo.perform.clock;

import com.sam.demo.perform.script.Story;

/**
 * @author masx
 *
 * <p> 时钟：是为进度监视设计
 * <p> 他监视每个 {@link com.sam.demo.perform.actor.Actor} 的动作 {@link com.sam.demo.perform.actor.Action}
 *
 * <p> 上层过程在 {@link com.sam.demo.perform.scene.StoryRobot#process(Story)}
 * <p> 对于实现者只要了解一个步进接口，一个异常接口
 *
 * @see com.sam.demo.perform.clock.SimpleClock
 * <p> 这个实现者已经接近实现业务，可供参考
 */
public interface Clock {
    /**
     * 步进接口
     * @param story 发生所在故事
     */
    void clock(Story story);

    /**
     * 异常接口
     * @param story 发生所在故事
     * @param msg 异常信息
     */
    void clock(Story story, String msg);
}
