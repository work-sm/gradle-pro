package com.sam.demo.nerver.neural.support;

import com.sam.demo.nerver.neural.entity.FaultTolerance;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * 容错支持类
 *
 * @author lry
 */
public class NeuralSupport {

	public static HystrixCommand.Setter buildByFaultTolerance() {
		return buildByFaultTolerance(new FaultTolerance());
	}
	
    /**
     * 根据容错配置构建Setter<p>
     * 
     * FaultTolerance --> HystrixCommand.Setter
     *
     * @param ft
     * @return
     */
    @SuppressWarnings("deprecation")
	public static HystrixCommand.Setter buildByFaultTolerance(FaultTolerance ft) {
        return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(ft.getFtKey()))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()

                        //熔断
                        .withCircuitBreakerEnabled(ft.isCbEnabled())
                        .withCircuitBreakerErrorThresholdPercentage(ft.getCbErrorThreshold())
                        .withCircuitBreakerForceClosed(ft.isCbForceClosed())
                        .withCircuitBreakerForceOpen(ft.isCbForceOpen())
                        .withCircuitBreakerRequestVolumeThreshold(ft.getCbReqVolThreshold())
                        .withCircuitBreakerSleepWindowInMilliseconds(ft.getCbSleepWindow())

                        //隔离
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(ft.getExecIsoMaxConcuReq())
                        .withExecutionIsolationThreadInterruptOnTimeout(ft.isExecIsoThreadInteTimeout())//线程中断超时开关
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.valueOf(ft.getExecIsoStrategy()))//策略模式
                        .withExecutionIsolationThreadTimeoutInMilliseconds(ft.getExecIsoThreadTimeout())//分发超时设置,相对其他容错模式多5秒

                        .withExecutionTimeoutInMilliseconds(ft.getExecTimeout())
                        .withExecutionTimeoutEnabled(ft.isExecTimeoutEnabled())//超时开关

                        .withMetricsRollingPercentileWindowInMilliseconds(ft.getMrpWindow())
                        .withMetricsRollingStatisticalWindowInMilliseconds(ft.getMrStatWindow())
                        .withMetricsRollingPercentileBucketSize(ft.getMrpBucketSize())
                        .withMetricsRollingPercentileEnabled(ft.isMrpEnabled())
                        .withMetricsRollingPercentileWindowBuckets(ft.getMrpWindowBuckets())
                        .withMetricsRollingStatisticalWindowBuckets(ft.getMrStatWindowBuckets())

                        .withMetricsHealthSnapshotIntervalInMilliseconds(ft.getMhSnapshotInterval())
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(ft.getFallbackIsoMaxConcuReq())
                        .withFallbackEnabled(ft.isFallbackEnabled())

                        .withRequestCacheEnabled(ft.isReqCacheEnabled())
                        .withRequestLogEnabled(ft.isReqLogEnabled()));


    }

}