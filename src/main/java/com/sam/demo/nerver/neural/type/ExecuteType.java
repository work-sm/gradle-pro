package com.sam.demo.nerver.neural.type;

/**
 * 执行类型
 * 
 * @author lry
 */
public enum ExecuteType {

	/**
	 * 常规路由服务成功
	 */
	RUN_ROUTE_SUCCESS,
	/**
	 * 常规路由服务失败
	 */
	RUN_ROUTE_FAILURE,
	
	/**
	 * 常规mock服务成功
	 */
	RUN_MOCK_SUCCESS,
	/**
	 * 常规mock服务失败
	 */
	RUN_MOCK_FAILURE,
	
	
	/******************************************/
	
	
	/**
	 * 失败容错路由服务成功
	 */
	FALLBACK_ROUTE_SUCCESS,
	/**
	 * 失败容错路由服务失败
	 */
	FALLBACK_ROUTE_FAILURE,
	
	/**
	 * 失败容错mock服务成功
	 */
	FALLBACK_MOCK_SUCCESS,
	/**
	 * 失败容错mock服务失败
	 */
	FALLBACK_MOCK_FAILURE,

	
	/******************************************/
	
	
	/**
	 * 呼吸服务成功
	 */
	BREATHCYCLE_SUCCESS,
	/**
	 * 呼吸服务失败
	 */
	BREATHCYCLE_FAILURE,
	
	/**
	 * 失败通知服务成功
	 */
	FAILNOTIFY_SUCCESS,
	/**
	 * 失败通知服务失败
	 */
	FAILNOTIFY_FAILURE,
	
	/**
	 * 回调服务成功
	 */
	CALLBACK_SUCCESS,
	/**
	 * 回调服务失败
	 */
	CALLBACK_FAILURE,
	
}
