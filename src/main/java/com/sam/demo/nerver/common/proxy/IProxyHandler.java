package com.sam.demo.nerver.common.proxy;

import java.lang.reflect.Method;

/**
 * 动态代理拦截器
 * 
 * @author lry
 */
public interface IProxyHandler<PREQ> {

	/**
	 * 执行过滤校验
	 * 
	 * @param req
	 * @return
	 */
	boolean filter(PREQ req);
	
	/**
	 * 执行前调用
	 * 
	 * @param req
	 */
	void before(PREQ req);
	
	/**
	 * 执行成功后调用
	 * 
	 * @param res
	 * @param method
	 * @param startTime 开始时间
	 */
	void success(Object res, Method method, long startTime);
	
	/**
	 * 异常调用
	 * 
	 * @param req
	 * @param method
	 * @param startTime
	 * @param t
	 */
	void error(PREQ req, Method method, long startTime, Throwable t);
	
	/**
	 * 执行后调用
	 * 
	 * @param res
	 * @param method
	 * @param startTime 开始时间
	 */
	void after(Object res, Method method, long startTime);
	
}
