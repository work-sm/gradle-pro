package com.sam.demo.nerver.neural.proxy.demo;

import com.sam.demo.nerver.neural.proxy.demo.handler.HelloWorldHandler;
import com.sam.demo.nerver.neural.proxy.demo.service.HelloWorld;
import com.sam.demo.nerver.neural.proxy.demo.service.impl.HelloWorldImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class HelloWorldTest {

    public static void main(String[] args) {
        HelloWorld helloWorld=new HelloWorldImpl();
        InvocationHandler handler=new HelloWorldHandler(helloWorld);
        
        //创建动态代理对象
        HelloWorld proxy=(HelloWorld)Proxy.newProxyInstance(
                helloWorld.getClass().getClassLoader(), 
                helloWorld.getClass().getInterfaces(), 
                handler);
        proxy.sayHelloWorld();
        proxy.exception("异常测试");
    }
}