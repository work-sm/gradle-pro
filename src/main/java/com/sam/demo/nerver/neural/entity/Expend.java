package com.sam.demo.nerver.neural.entity;

import com.sam.demo.nerver.common.SystemClock;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 耗时信息
 * 
 * @author lry
 */
public class Expend {

	/**
	 * 统计时间戳
	 */
	public long time;
	/**
	 * 总耗时(单位:ms)
	 */
	private AtomicLong expend;
	/**
	 * 路由耗时(单位:ms)
	 */
	private AtomicLong route;
	/**
	 * mock耗时(单位:ms)
	 */
	private AtomicLong mock;
	/**
	 * 呼吸耗时(单位:ms)
	 */
	private AtomicLong breathCycle;
	/**
	 * 失败通知耗时(单位:ms)
	 */
	private AtomicLong failNotify;
	/**
	 * 异步通知结果耗时(单位:ms)
	 */
	private AtomicLong callback;
	/**
	 * 代理创建耗时
	 */
	private AtomicLong proxy;
	
	public Expend() {
		this.setExpend(new AtomicLong(0));
		this.setRoute(new AtomicLong(0));
		this.setMock(new AtomicLong(0));
		this.setBreathCycle(new AtomicLong(0));
		this.setFailNotify(new AtomicLong(0));
		this.setCallback(new AtomicLong(0));
		this.setProxy(new AtomicLong(0));
		
		this.setTime(SystemClock.now());
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public AtomicLong getExpend() {
		return expend;
	}

	public void setExpend(AtomicLong expend) {
		this.expend = expend;
	}

	public AtomicLong getRoute() {
		return route;
	}

	public void setRoute(AtomicLong route) {
		this.route = route;
	}

	public AtomicLong getMock() {
		return mock;
	}

	public void setMock(AtomicLong mock) {
		this.mock = mock;
	}

	public AtomicLong getBreathCycle() {
		return breathCycle;
	}

	public void setBreathCycle(AtomicLong breathCycle) {
		this.breathCycle = breathCycle;
	}

	public AtomicLong getFailNotify() {
		return failNotify;
	}

	public void setFailNotify(AtomicLong failNotify) {
		this.failNotify = failNotify;
	}

	public AtomicLong getCallback() {
		return callback;
	}

	public void setCallback(AtomicLong callback) {
		this.callback = callback;
	}

	public AtomicLong getProxy() {
		return proxy;
	}

	public void setProxy(AtomicLong proxy) {
		this.proxy = proxy;
	}

	@Override
	public String toString() {
		return "Expend [time=" + time + ", expend=" + expend + ", route="
				+ route + ", mock=" + mock + ", breathCycle=" + breathCycle
				+ ", failNotify=" + failNotify + ", callback=" + callback + ", proxy=" + proxy + "]";
	}
	
}
