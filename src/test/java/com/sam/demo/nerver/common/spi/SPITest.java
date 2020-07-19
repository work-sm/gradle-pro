package com.sam.demo.nerver.common.spi;


public class SPITest {

	public static void main(String[] args) {
		TestExt impl = ExtensionLoader.getDefaultExtensionLoader(TestExt.class).getExtension();
		System.out.println(impl);
	}

}
