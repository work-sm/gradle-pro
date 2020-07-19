package com.sam.demo.neural.bloomfilter;

import lombok.extern.slf4j.Slf4j;
import com.sam.demo.neural.AbstractNeural;
import com.sam.demo.neural.NeuralContext;
import com.sam.demo.neural.OriginalCall;
import com.sam.demo.neural.bloomfilter.core.NeuralBitSet;
import com.sam.demo.neural.common.URL;
import com.sam.demo.neural.extension.Extension;
import com.sam.demo.neural.extension.ExtensionLoader;

/**
 * The Bloom Filter
 *
 * @author lry
 */
@Slf4j
@Extension(BloomFilterGlobalConfig.IDENTITY)
public class BloomFilter extends AbstractNeural<BloomFilterConfig, BloomFilterGlobalConfig> {

    private BloomFilterFactory<String> bloomFilterFactory;

    @Override
    public void initialize(URL url) {
        super.initialize(url);

        double falsePositiveProbability = 0.0001;
        int expectedNumberOfElements = 10000;
        this.bloomFilterFactory = new BloomFilterFactory<>(falsePositiveProbability, expectedNumberOfElements);

        NeuralBitSet neuralBitSet = ExtensionLoader.getLoader(NeuralBitSet.class).getExtension();
        bloomFilterFactory.bind(neuralBitSet);
    }

    @Override
    public Object wrapperCall(NeuralContext neuralContext, String identity, OriginalCall originalCall) throws Throwable {
        try {
            if (bloomFilterFactory.contains(neuralContext.getId())) {
                throw new RuntimeException("Repeated requests");
            }

            bloomFilterFactory.add(neuralContext.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return super.wrapperCall(neuralContext, identity, originalCall);
    }

}
