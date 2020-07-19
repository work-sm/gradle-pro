package com.sam.demo.nerver.neural.route;

import com.sam.demo.nerver.neural.type.ExecuteType;

/**
 * <b style="color:red;font-size:24px">神经元路由器</b>
 * 
 * @see 注意事项:route、mock...等模块中出现的异常必须向外抛出,否则会影响正常流程.
 * 
 * @author lry
 *
 * @param <REQ> 请求对象
 * @param <RES> 响应对象
 */
public interface IRoute<REQ, RES> {

	/**
	 * 远程调度
	 * <p>
	 * <b style="color:green;font-size:14px">使用场景:源码通信、业务处理</b>
	 * @param neuralId 神经元消息ID
	 * @param req 请求对象
	 * @return
	 * @throws Throwable
	 */
	RES route(String neuralId, REQ req) throws Throwable;

	/**
	 * Mock放通服务(可作为服务降级处理)
	 * <p>
	 * <b style="color:green;font-size:14px">使用场景:服务降级、服务放通、服务屏蔽</b>
	 * @param neuralId 神经元消息ID
	 * @param req 请求对象
	 * @return
	 * @throws Throwable
	 */
	RES mock(String neuralId, REQ req) throws Throwable;
	
	/**
	 * 呼吸周期(休眠重试周期)计算方式
	 * <p>
	 * 为保证服务端能够有充足的时间释放句柄资源,首次或每次重试之前都休眠指定的时间后发起,
	 * 而且重试的频率因适当随时间戳和重试次数进行适当的延长,进入节省资源的消耗。
	 * <p>
	 * <b style="color:green;font-size:14px">使用场景:伸缩性休眠重试</b>
	 * <p>
	 * 注意:呼吸异常,会打印异常信息,但不会向外抛出异常
	 * @param neuralId 神经元消息ID
	 * @param times 当前已重试次数
	 * @param maxRetryNum 最大重试次数,-1表示无限重试
	 * @param cycle 呼吸基数(毫秒)
	 * @param expend 容错累积耗时(毫秒)
	 * @return
	 */
	long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long expend) throws Throwable;
	
	/**
	 * 异步回调
	 * <p>
	 * <b style="color:green;font-size:14px">使用场景:异步通知、结果通知</b>
	 * @param neuralId 神经元消息ID
	 * @param execType 执行类型
	 * @param res 响应对象
	 */
	void callback(String neuralId, ExecuteType execType, RES res) throws Throwable;
	
	/**
	 * 失败通知
	 * <p>
	 * <b style="color:green;font-size:14px">使用场景:监控、告警</b>
	 * <p>
	 * 注意:通知异常,会打印异常信息,但不会向外抛出异常
	 * @param neuralId 神经元消息ID
	 * @param execType 执行类型
	 * @param req 请求对象
	 * @param t 异常
	 */
	void failNotify(String neuralId, ExecuteType execType, REQ req, Throwable t) throws Throwable;
	
}
