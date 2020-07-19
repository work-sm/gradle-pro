package com.sam.demo.nerver.strategy.entity;

import com.sam.demo.nerver.neural.entity.FaultTolerance;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.route.IRoute;

/**
 * 传输对象
 * 
 * @author lry
 *
 * @param <REQ>
 * @param <RES>
 */
public class StrategyDo<REQ, RES> {

	private REQ req;
	private NeuralConf conf;
	private FaultTolerance ft;
	private IRoute<REQ, RES> route;
	
	public StrategyDo() {
	}
	public StrategyDo(FaultTolerance ft, NeuralConf conf, IRoute<REQ, RES> route,REQ req) {
		this.ft = ft;
		this.conf = conf;
		this.req = req;		
		this.route = route;
	}
	
	public REQ getReq() {
		return req;
	}
	public void setReq(REQ req) {
		this.req = req;
	}
	public NeuralConf getConf() {
		return conf;
	}
	public void setConf(NeuralConf conf) {
		this.conf = conf;
	}
	public FaultTolerance getFt() {
		return ft;
	}
	public void setFt(FaultTolerance ft) {
		this.ft = ft;
	}
	public IRoute<REQ, RES> getRoute() {
		return route;
	}
	public void setRoute(IRoute<REQ, RES> route) {
		this.route = route;
	}
	
	@Override
	public String toString() {
		return "FtDo [req=" + req + ", conf=" + conf + ", ft=" + ft + ", route=" + route + "]";
	}
	
}
