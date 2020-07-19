package com.sam.demo.nerver.common.proxy;

import com.sam.demo.nerver.common.SystemClock;
import com.sam.demo.nerver.common.exception.FilterFailureException;
import com.sam.demo.nerver.common.exception.ProxyException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理工厂
 * 
 * @author lry
 */
public class ProxyFactory<REQ,RES> {
	
	public RES build(final IProxyHandler<REQ> hander, final REQ req) throws Throwable {
		return build(hander, this.getClass().getClassLoader(), req);
	}
	
	@SuppressWarnings("unchecked")
	public RES build(final IProxyHandler<REQ> hander,ClassLoader classLoader, final REQ req) throws Throwable {
        InvocationHandler handler = new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(hander.filter(req)){
					hander.before(req);
					long startTime=SystemClock.now();
					Object res=null;
					try {
						res= method.invoke(req, args);
						hander.success(res,method,startTime);
						hander.after(res,method,startTime);
						return res;
					}catch(Throwable t){
						hander.error(req, method, startTime, t);
						hander.after(res,method,startTime);
						throw new ProxyException(t);
					}
				}else{
					throw new FilterFailureException("Filter is fail.");
				}
			}
		};
        return (RES) Proxy.newProxyInstance(classLoader, req.getClass().getInterfaces(), handler);
	}
	
}
