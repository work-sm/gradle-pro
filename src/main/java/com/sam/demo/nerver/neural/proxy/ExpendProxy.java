package com.sam.demo.nerver.neural.proxy;

import com.sam.demo.nerver.common.SystemClock;
import com.sam.demo.nerver.common.proxy.IProxyHandler;
import com.sam.demo.nerver.neural.entity.Expend;
import com.sam.demo.nerver.neural.route.IRoute;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 耗时统计代理
 * 
 * @author lry
 * @param <REQ>
 * @param <RES>
 */
public class ExpendProxy<REQ, RES> implements IProxyHandler<IRoute<REQ, RES>> {

	public final static String ROUTE_KEY="route";
	public final static String MOCK_KEY="mock";
	public final static String BREATHCYCLE_KEY="breathCycle";
	public final static String CALLBACK_KEY="callback";
	public final static String FAILNOTIFY_KEY="failNotify";
	
	private Expend expend=new Expend();
	
	public Expend getExpend() {
		return expend;
	}
	public void setExpend(Expend expend) {
		this.expend=expend;
	}

	public boolean filter(IRoute<REQ, RES> req) {
		return true;
	}

	public void before(IRoute<REQ, RES> req) {
		
	}

	public void success(Object res, Method method, long startTime) {
		
	}

	public void after(Object res, Method method, long startTime) {
		long expendTime=SystemClock.now()-startTime;
		switch (method.getName()) {
		case ROUTE_KEY:
			expend.setRoute(new AtomicLong(expend.getRoute().addAndGet(expendTime)));
			break;
		case MOCK_KEY:
			expend.setMock(new AtomicLong(expend.getMock().addAndGet(expendTime)));
			break;
		case BREATHCYCLE_KEY:
			expend.setBreathCycle(new AtomicLong(expend.getBreathCycle().addAndGet(expendTime)));
			break;
		case CALLBACK_KEY:
			expend.setCallback(new AtomicLong(expend.getCallback().addAndGet(expendTime)));
			break;
		case FAILNOTIFY_KEY:
			expend.setFailNotify(new AtomicLong(expend.getFailNotify().addAndGet(expendTime)));
			break;
		default://忽略其他
			break;
		}
	}

	public void error(IRoute<REQ, RES> req, Method method, long startTime, Throwable t) {
		
	}

}
