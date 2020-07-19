package com.sam.demo.nerver.common.spi.impl;

import com.sam.demo.nerver.common.spi.TestExt;

public class TestExtImpl3 implements TestExt {

	@Override
	public String echo(String str) {
		return "TestExtImpl3--->"+str;
	}

}
