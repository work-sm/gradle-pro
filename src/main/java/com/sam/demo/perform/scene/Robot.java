package com.sam.demo.perform.scene;

/**
 * @author masx
 *
 * <p> 这个接口只有一个实现，除非改变结构，不会需要拓展
 * <p> 是真实解析故事中的步骤并执行，执行的实际内容并不关心，只是按步骤通知每个演员入场出场
 *
 * @see com.sam.demo.perform.scene.StoryRobot
 *
 * <p> 也可以观察他们在每个场景的用法
 * @see com.sam.demo.perform.scene.impl
 */
public interface Robot<S> {

    void process(S s) throws Exception;

    void close();

}
