package com.sam.demo.nerver.degrade;

import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.entity.RouteExpendProxy;

/**
 * 服务降级
 * <p>
 * 服务降级分类:<p>
 * 1.直接屏蔽降级<p>
 * 2.快速容错降级<p>
 * 3.自定义业务降级
 * <p>
 * 降级策略:<p>
 * 1.返回空<p>
 * 2.抛异常<p>
 * 3.本地mock<p>
 * 4.自定义策略
 * 
 * @author lry
 */
public interface IDegrade<REQ, RES> {

	/**
	 * 服务降级
	 * 
	 * @param conf
	 * @param rep
	 * @param req
	 * @return
	 * @throws Throwable
	 */
	public RES degrade(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req) throws Throwable;
	
	/**
	 * 业务降级
	 * 
	 * @param conf
	 * @param rep
	 * @param req
	 * @return
	 * @throws Throwable
	 */
	public RES biz(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req) throws Throwable;
	
}
