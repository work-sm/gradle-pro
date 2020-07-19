package com.sam.demo.nerver.idempotent.handler;

import com.sam.demo.nerver.common.NamedThreadFactory;
import com.sam.demo.nerver.common.SystemClock;
import com.sam.demo.nerver.idempotent.Idempotent;
import com.sam.demo.nerver.idempotent.entity.IdempotentStorage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 幂等处理中心
 * 
 * @author lry
 *
 * @param <REQ>
 * @param <RES>
 */
public class IdempotentHandler<REQ, RES> implements Idempotent<REQ, RES>{

	private static final Logger logger=LoggerFactory.getLogger(IdempotentHandler.class);
	
	/**
	 * 检查周期
	 */
	private long retryPeriod=1000*60;
	/**
	 * 过期周期
	 */
	private long expireCycle=1000*60*2;
	/**
	 * 容量大小
	 */
	private int idempStorCapacity=10000;
	/**
	 * 持久化仓库
	 */
	private ConcurrentHashMap<String, IdempotentStorage<RES>> idempotentStorage;
	/**
	 * 失败重试定时器，定时检查是否有请求失败，如有，无限次重试
	 */
    private ScheduledFuture<?> retryFuture;
    /**
     * 定时任务执行器
     */
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("DubboRegistryFailedRetryTimer", true));
    
	public IdempotentHandler(long expireCycle, int idempStorCapacity) {
		this.expireCycle=expireCycle;
		this.idempStorCapacity=idempStorCapacity;
		try {
			init();//初始化
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * 清理
	 * 
	 * @throws Throwable
	 */
	protected void cleanup() throws Throwable {
		if(idempotentStorage.isEmpty()){
			return;
		}
		
		for (Map.Entry<String, IdempotentStorage<RES>> entry:idempotentStorage.entrySet()) {
			IdempotentStorage<RES> isStorage=entry.getValue();
			if(isStorage!=null){
				if(SystemClock.now()-isStorage.getTime()>expireCycle){
					idempotentStorage.remove(isStorage.getId());
				}
			}
		}
	}

	@Override
	public void init() throws Throwable {
		idempotentStorage=new ConcurrentHashMap<String, IdempotentStorage<RES>>(idempStorCapacity);
		try {
			init();//初始化
	        this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
	            public void run() {
	                // 检测并连接注册中心
	                try {
	                    cleanup();
	                } catch (Throwable t) { // 防御性容错
	                    logger.error("Unexpected error occur at failed retry, cause: " + t.getMessage(), t);
	                }
	            }
	        }, retryPeriod, retryPeriod, TimeUnit.MILLISECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public RES idempotent(String id) throws Throwable {
		IdempotentStorage<RES> storage= idempotentStorage.get(id);
		return storage==null?null:storage.getRes();
	}

	@Override
	public void storage(String id, REQ req, RES res) throws Throwable {
		idempotentStorage.put(id, new IdempotentStorage<RES>(id, SystemClock.now(), res));
	}
	
	@Override
	public void destroy() {
		try {
			if(idempotentStorage!=null){
				idempotentStorage.clear();	
			}
			if(retryFuture!=null){
				retryFuture.cancel(true);	
			}
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
	}

}
