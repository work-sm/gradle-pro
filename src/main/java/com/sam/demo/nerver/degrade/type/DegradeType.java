package com.sam.demo.nerver.degrade.type;

/**
 * 服务降级分类
 * 
 * @author lry
 */
public enum DegradeType {

	/**
	 * 屏蔽降级
	 */
	SHIELDING,
	
	/**
	 * 容错降级
	 */
	FAULTTOLERANT,
	
	/**
	 * 业务降级
	 */
	BUSINESS;
	
}
