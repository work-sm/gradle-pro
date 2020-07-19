package com.sam.demo.nerver.degrade;

import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.entity.RouteExpendProxy;

/**
 * 业务服务降级
 * 
 * @author lry
 */
public interface IBizDegrade<REQ, RES> {

	/**
	 * 业务降级
	 * 
	 * @param conf
	 * @param route
	 * @param req
	 * @return
	 * @throws Throwable
	 */
	public RES biz(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req) throws Throwable;
	
}
