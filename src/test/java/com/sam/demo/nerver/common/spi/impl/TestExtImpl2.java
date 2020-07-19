package com.sam.demo.nerver.common.spi.impl;

import com.sam.demo.nerver.common.spi.TestExt;

public class TestExtImpl2 implements TestExt {

	@Override
	public String echo(String str) {
		return "TestExtImpl2--->"+str;
	}

}
