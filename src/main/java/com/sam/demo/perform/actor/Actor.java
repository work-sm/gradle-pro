package com.sam.demo.perform.actor;

import com.sam.demo.perform.script.Story;

/**
 * @author masx
 *
 * <p> 演员类：持有pc硬盘唯一资源（exe,file）
 * <p> 在一个演员执行时，他无法并行处理第二件事，所以必须对此类本身做控制
 * <p> 一个正常的操作单元应该是
 *
 * this.invite() -> this.visit(Story) -> this.release()
 * 锁定 -> 访问 -> 释放
 * 这个过程为上层封装{@link com.sam.demo.perform.scene.StoryRobot#process(Story)}
 *
 * <p> 实际上我们使用关心这个资源是怎么执行的
 * @see com.sam.demo.perform.actor.impl.NopActor#visit(Story)
 *
 */
public interface Actor {

    /**
     * actor 必须有全局唯一命名，不然哪个actor 锁定和释放将会混乱
     * @return
     */
    String name();

    void visit(Story story) throws Exception;

    void invite() throws Exception;

    void release();

    /**
     * 如果 actor 持有IO 等占有性资源，需要在结束时释放
     * @throws Exception
     */
    void close() throws Exception;

}
