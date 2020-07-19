package com.sam.demo.nerver.common.cache;


import com.sam.demo.nerver.common.cache.support.FIFOCache;
import com.sam.demo.nerver.common.cache.support.LFUCache;
import com.sam.demo.nerver.common.cache.support.LRUCache;
import com.sam.demo.nerver.common.cache.support.TimedCache;
import org.junit.Test;

/**
 * 缓存使用Demo
 */
public class CacheTest {
	
	public void testFIFOCache() throws InterruptedException{
		FIFOCache<String,String> fifoCache = new FIFOCache<String, String>(3, 0);
		fifoCache.put("key1", "value1", 1000 * 3);
		fifoCache.put("key2", "value2", 1000 * 3);
		fifoCache.put("key3", "value3", 1000 * 3);
		fifoCache.put("key4", "value4", 1000 * 3);
		
		//由于缓存容量只有3，当加入第四个元素的时候，根据FIFO规则，最先放入的对象将被移除，于是
		for (String value : fifoCache) {
			System.out.println(value);
		}
		
		//设置了每个元素的超时时间是3秒，当4秒后此对象便被移除了
		System.out.println("Before expire: " + fifoCache.get("key1"));
		System.out.println("Sleep 4s...");
		Thread.sleep(1000 * 4);
		System.out.println("After expire: " + fifoCache.get("key1"));
	}
	
	public void testLFUCache() {
		LFUCache<String, String> lfuCache = new LFUCache<String, String>(3);
		lfuCache.put("key1", "value1", 1000 * 3);
		lfuCache.get("key1");//使用次数+1
		lfuCache.put("key2", "value2", 1000 * 3);
		lfuCache.put("key3", "value3", 1000 * 3);
		lfuCache.put("key4", "value4", 1000 * 3);
		
		//由于缓存容量只有3，当加入第四个元素的时候，根据LRU规则，最少使用的将被移除（2,3被移除）
		for (String value : lfuCache) {
			System.out.println(value);
		}
	}
	
	public void testLRUCache() {
		LRUCache<String, String> lruCache = new LRUCache<String, String>(3);
		lruCache.put("key1", "value1", 1000 * 3);
		lruCache.put("key2", "value2", 1000 * 3);
		lruCache.put("key3", "value3", 1000 * 3);
		lruCache.get("key1");//使用时间推近
		lruCache.put("key4", "value4", 1000 * 3);
		
		//由于缓存容量只有3，当加入第四个元素的时候，根据LRU规则，最少使用的将被移除（2被移除）
		for (String value : lruCache) {
			System.out.println(value);
		}
		
	}
	
	@Test
	public void testTimedCache() throws InterruptedException {
		TimedCache<String, String> timedCache = new TimedCache<String, String>(1000 * 3);
		//启动定时任务，每4秒检查一次过期
		timedCache.schedulePrune(1000 * 3);
		
		for (int i = 0; i < 10; i++) {
			timedCache.put("key"+i, "value"+i, 1000 * (i+1));
		}
		
		while (!timedCache.isEmpty()) {
			//四秒后由于value2设置了100秒过期，其他设置了三秒过期，因此只有value2被保留下来
			for (String value : timedCache) {
				System.out.println(value);
			}
			Thread.sleep(1000);
			System.out.println("Sleep 1s...");
		}
		
		//取消定时清理
		timedCache.cancelPruneSchedule();
	}
	
	public static <V> void main(String[] args) throws InterruptedException {
		CacheTest cache=new CacheTest();
		cache.testFIFOCache();
		cache.testLFUCache();
		cache.testLRUCache();
		cache.testTimedCache();
	}
}
