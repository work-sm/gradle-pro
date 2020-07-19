package com.sam.demo.nerver.slow.support;

import com.sam.demo.nerver.slow.ISlow;

/**
 * 对数函数
 * y=log(a)x
 * 
 * @author lry
 */
public class LogarithmSlow implements ISlow<Double, Double, Double> {

	private Double sys;

	public void init(Double sys) {
		this.sys=sys;
	}

	public Double function(Double x) {
		return Math.log(x)/Math.log(sys);
	}
	
}
