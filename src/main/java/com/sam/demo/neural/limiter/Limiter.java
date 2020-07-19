package com.sam.demo.neural.limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sam.demo.neural.NeuralContext;
import com.sam.demo.neural.config.GlobalConfig;
import com.sam.demo.neural.config.event.EventCollect;
import com.sam.demo.neural.OriginalCall;
import com.sam.demo.neural.AbstractNeural;
import com.sam.demo.neural.extension.Extension;
import com.sam.demo.neural.extension.ExtensionLoader;
import com.sam.demo.neural.limiter.core.ILimiter;
import com.sam.demo.neural.limiter.LimiterGlobalConfig.EventType;
import lombok.extern.slf4j.Slf4j;

/**
 * The Limiter.
 *
 * @author lry
 **/
@Slf4j
@Extension(LimiterGlobalConfig.IDENTITY)
public class Limiter extends AbstractNeural<LimiterConfig, LimiterGlobalConfig> {

    private final ConcurrentMap<String, ILimiter> limiters = new ConcurrentHashMap<>();

    @Override
    public void addConfig(LimiterConfig config) {
        super.addConfig(config);
        ILimiter limiter = ExtensionLoader.getLoader(ILimiter.class).getExtension(config.getModel());
        limiters.put(config.identity(), limiter);
    }

    @Override
    public Object wrapperCall(NeuralContext neuralContext, String identity, OriginalCall originalCall) throws Throwable {
        super.wrapperCall(neuralContext, identity, originalCall);

        // The check global config of limiter
        if (null == globalConfig || null == globalConfig.getEnable() ||
                GlobalConfig.Switch.OFF == globalConfig.getEnable()) {
            return originalCall.call();
        }

        // The check limiter object
        if (null == identity || !limiters.containsKey(identity)) {
            return originalCall.call();
        }

        return limiters.get(identity).wrapperCall(neuralContext, originalCall);
    }

    @Override
    public Map<String, Map<String, Long>> collect() {
        Map<String, Map<String, Long>> dataMap = super.collect();
        try {
            limiters.forEach((identity, limiter) -> {
                Map<String, Long> tempDataMap = limiter.getStatistics().getAndReset();
                if (null == tempDataMap || tempDataMap.isEmpty()) {
                    return;
                }

                dataMap.put(identity, tempDataMap);
            });
        } catch (Exception e) {
            EventCollect.onEvent(EventType.COLLECT_EXCEPTION);
            log.error(EventType.COLLECT_EXCEPTION.getMessage(), e);
        }

        return dataMap;
    }

    @Override
    public Map<String, Map<String, Long>> statistics() {
        Map<String, Map<String, Long>> dataMap = super.collect();
        try {
            limiters.forEach((identity, limiter) -> {
                Map<String, Long> tempDataMap = limiter.getStatistics().getStatisticsData();
                if (null == tempDataMap || tempDataMap.isEmpty()) {
                    return;
                }

                dataMap.put(identity, tempDataMap);
            });
        } catch (Exception e) {
            EventCollect.onEvent(EventType.COLLECT_EXCEPTION);
            log.error(EventType.COLLECT_EXCEPTION.getMessage(), e);
        }

        return dataMap;
    }

    @Override
    protected void ruleNotify(String identity, LimiterConfig ruleConfig) {
        super.ruleNotify(identity, ruleConfig);

        try {
            ILimiter limiter = limiters.get(identity);
            if (null == limiter) {
                log.warn("The limiter config is notify is exception, not found limiter:[{}]", identity);
                return;
            }

            boolean flag = limiter.refresh(ruleConfig);
            if (!flag) {
                log.warn("The limiter refresh failure:{},{},{}", identity, globalConfig, ruleConfig);
            }
        } catch (Exception e) {
            EventCollect.onEvent(EventType.NOTIFY_EXCEPTION);
            log.error(EventType.NOTIFY_EXCEPTION.getMessage(), e);
        }
    }

}
