package com.sam.demo.nerver.neural.entity;

import com.sam.demo.nerver.neural.route.IRoute;

public class RouteExpendProxy<REQ, RES> {
	
	private Expend expend;
	private IRoute<REQ, RES> route;
	
	public RouteExpendProxy(Expend expend,IRoute<REQ, RES> route) {
		this.expend=expend;
		this.route=route;
	}
	
	public Expend getExpend() {
		return expend;
	}
	public void setExpend(Expend expend) {
		this.expend = expend;
	}
	public IRoute<REQ, RES> getRoute() {
		return route;
	}
	public void setRoute(IRoute<REQ, RES> route) {
		this.route = route;
	}
	
}
