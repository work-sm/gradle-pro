package com.sam.demo.neural.extension.singleton;

import com.sam.demo.neural.extension.SPI;

@SPI(single = true)
public interface NpiSingleton {
	long spiHello();
}
