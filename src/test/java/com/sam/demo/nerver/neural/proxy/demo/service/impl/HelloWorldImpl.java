package com.sam.demo.nerver.neural.proxy.demo.service.impl;

import com.sam.demo.nerver.neural.proxy.demo.service.HelloWorld;

/**
* 类HelloWorldImpl是HelloWorld接口的实现
* 
* @author lry
*/
public class HelloWorldImpl implements HelloWorld{

   public void sayHelloWorld() {
       System.out.println("HelloWorld!");
   }

	public String exception(String msg) {
		throw new RuntimeException(msg);
	}

}