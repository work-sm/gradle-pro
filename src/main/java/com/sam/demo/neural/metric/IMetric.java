package com.sam.demo.neural.metric;

import com.sam.demo.neural.extension.SPI;

import java.util.Map;

@SPI(single = true)
public interface IMetric {

    /**
     * The get metric
     */
    Map<String, Object> getMetric();

}
