package com.sam.demo.nerver.strategy.support;

import com.sam.demo.nerver.common.NamedThreadFactory;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.strategy.IStrategy;
import com.sam.demo.nerver.strategy.StrategyHandler;
import com.sam.demo.nerver.strategy.entity.StrategyDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 5.失败恢复(Failback)<p>
 * 失败自动恢复，后台记录失败请求，定时重发<p>
 * 通常用于消息通知操作<p>
 * 
 * @author lry
 *
 * @param <REQ>
 * @param <RES>
 */
public class Failback<REQ, RES> implements IStrategy<REQ, RES> {

	private static final Logger logger=LoggerFactory.getLogger(Failback.class);
	
	 // 定时任务执行器
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("FailbackRetryTimer", true));

    private final ConcurrentMap<String, StrategyDo<REQ, RES>> failedFtDoMap = new ConcurrentHashMap<String, StrategyDo<REQ, RES>>();
    
    // 失败重试定时器，定时检查是否有请求失败，如有，无限次重试
    private final ScheduledFuture<?> retryFuture;
	
    public Failback() {
    	int retryPeriod = 3000;
        this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                // 重试发送
                try {
                    retry();
                } catch (Throwable t) { // 防御性容错
                    logger.error("Unexpected error occur at failed retry, cause: " + t.getMessage(), t);
                }
            }
        }, retryPeriod, retryPeriod, TimeUnit.MILLISECONDS);
	}
    
    @Override
	public RES execute(StrategyDo<REQ, RES> ftDo) {
    	ftDo.setConf(new NeuralConf(1, 0, true));
    	
    	try {
        	StrategyHandler<REQ, RES> handler=new StrategyHandler<REQ, RES>();
    		return handler.execute(ftDo);
		} catch (Throwable t) {
			logger.error("Failed to execute " + ftDo + ", waiting for retry, cause: " + t.getMessage(), t);
			failedFtDoMap.put(UUID.randomUUID().toString(), ftDo);
		}
    	
		return null;
	}
    
    private void retry(){
    	if (! failedFtDoMap.isEmpty()) {
            Map<String, StrategyDo<REQ, RES>> failed = new HashMap<String, StrategyDo<REQ, RES>>(failedFtDoMap);
            for (Map.Entry<String, StrategyDo<REQ, RES>> entry : new HashMap<String, StrategyDo<REQ, RES>>(failed).entrySet()) {
                if (entry.getValue() == null) {
                    failed.remove(entry.getKey());
                }
            }
            if (failed.size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Retry execute " + failed);
                }
                try {
                    for (Map.Entry<String, StrategyDo<REQ, RES>> entry : failed.entrySet()) {
                    	StrategyDo<REQ, RES> ftDo = entry.getValue();
                    	try {
                    		execute(ftDo);
                        } catch (Throwable t) { // 忽略所有异常，等待下次重试
                            logger.warn("Failed to retry execute " + ftDo + ", waiting for again, cause: " + t.getMessage(), t);
                        }
                    }
                } catch (Throwable t) { // 忽略所有异常，等待下次重试
                    logger.warn("Failed to retry execute " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                }
            }
        }
    }
    
    protected void recover() throws Exception {
    	// subscribe
        Map<String, StrategyDo<REQ, RES>> recoverSubscribed = new HashMap<String, StrategyDo<REQ, RES>>(failedFtDoMap);
        if (! recoverSubscribed.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Recover subscribe url " + recoverSubscribed.keySet());
            }
            for (Map.Entry<String, StrategyDo<REQ, RES>> entry : recoverSubscribed.entrySet()) {
            	StrategyDo<REQ, RES> ftDo = entry.getValue();
            	try {
            		execute(ftDo);
                } catch (Throwable t) { // 忽略所有异常，等待下次重试
                    logger.warn("Failed to retry execute " + ftDo + ", waiting for again, cause: " + t.getMessage(), t);
                }
            }
        }
    }
    
    public Future<?> getRetryFuture() {
        return retryFuture;
    }
    
    public void destroy() {
        try {
            retryFuture.cancel(true);
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }
    
}