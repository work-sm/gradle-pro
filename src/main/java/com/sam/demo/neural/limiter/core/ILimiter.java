package com.sam.demo.neural.limiter.core;

import com.sam.demo.neural.NeuralContext;
import com.sam.demo.neural.OriginalCall;
import com.sam.demo.neural.extension.SPI;
import com.sam.demo.neural.limiter.LimiterConfig;
import com.sam.demo.neural.limiter.LimiterStatistics;

/**
 * The Limiter Interface.
 *
 * @author lry
 */
@SPI("local")
public interface ILimiter {

    /**
     * The refresh in-memory data.
     *
     * @param limiterConfig The LimiterConfig
     * @return true is success
     * @throws Exception The Exception is execute refresh LimiterConfig
     */
    boolean refresh(LimiterConfig limiterConfig) throws Exception;

    /**
     * The process original call.
     *
     * @param neuralContext {@link NeuralContext}
     * @param originalCall  {@link OriginalCall}
     * @return The object of OriginalCall
     * @throws Throwable The Exception is execute doOriginalCall
     */
    Object wrapperCall(NeuralContext neuralContext, OriginalCall originalCall) throws Throwable;

    /**
     * The get statistics of limiter.
     *
     * @return The Limiter Statistics
     */
    LimiterStatistics getStatistics();

}
