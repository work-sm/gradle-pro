package com.sam.demo.neural.circuitbreaker.handler;

/**
 * The Proxy Factory
 *
 * @author lry
 */
public class ProxyFactory {

    public static <T> T proxyBean(Object target) {
        return (T) new CircuitBreakerInvocationHandler(target).proxy();
    }

}
