package com.sam.demo.nerver.common.spi.impl;

import com.sam.demo.nerver.common.spi.TestExt;

public class TestExtImpl1 implements TestExt {

	public TestExtImpl1() {
		System.out.println("TestExtImpl1sss");
	}
	
	@Override
	public String echo(String str) {
		return "TestExtImpl1--->"+str;
	}

}
