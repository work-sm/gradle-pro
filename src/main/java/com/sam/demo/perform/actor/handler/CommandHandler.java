package com.sam.demo.perform.actor.handler;

/**
 * @author masx
 *
 * <p> 有时调用 exe 可能不止是双击exe 或许后面会有 -params
 * <p> 可以看到只有一个接口，接受和返回都是String
 * <p> 实际上就是参数字符串形式拼接
 *
 * @see com.sam.demo.perform.actor.handler.ParamHandler#handle(String)
 *
 * <p> 他在 {@link com.sam.demo.perform.actor.Executor} 101 中改变执行参数
 *
 */
public interface CommandHandler {
    String handle(String command);
}
