package com.sam.demo.perform.script;

import com.sam.demo.perform.actor.Actor;

/**
 * @author masx
 *
 * <p> 剧本类 : 承载了一次完整过程调用调用
 * <p> 具体可参考 {@link com.sam.demo.perform.script.Story}
 */
public interface Script {

    /**
     * 向前发展一个事件
     */
    void stepping();

    /**
     * 剧本可被某个演员访问，以做出相应动作
     * @param actor 访问者
     * @throws Exception
     */
    void accept(Actor actor) throws Exception;

}
