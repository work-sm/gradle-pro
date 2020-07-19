package com.sam.demo.nerver.slow.support;

import com.sam.demo.nerver.slow.ISlow;
import com.sam.demo.nerver.slow.entity.LinearEntry;

/**
 * 线性函数
 * y=ax+b
 * 
 * @author lry
 */
public class LinearSlow implements ISlow<LinearEntry, Double, Double> {

	private LinearEntry sys;

	public void init(LinearEntry sys) {
		this.sys=sys;
	}

	public Double function(Double x) {
		return sys.getA()*x+sys.getB();
	}
	
}
