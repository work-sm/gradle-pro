package com.sam.demo.nerver.degrade.type;

/**
 * 策略类型
 * 
 * @author lry
 */
public enum StrategyType {
	
	/**
	 * 返回null
	 */
	NULL,
	
	/**
	 * 抛异常
	 */
	EXCEPTION,
	
	/**
	 * 本地mock服务
	 */
	MOCK;
	
}
