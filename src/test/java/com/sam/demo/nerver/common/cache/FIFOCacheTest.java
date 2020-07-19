package com.sam.demo.nerver.common.cache;

import com.sam.demo.nerver.common.cache.support.FIFOCache;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class FIFOCacheTest {

	@Test
	public void testCache() {
		Cache<String, String> cache = new FIFOCache<>(3);
		assertEquals(3, cache.capacity());
		assertEquals(0, cache.size());

		cache.put("1", "1");
		cache.put("2", "2");
		assertEquals(2, cache.size());
		assertFalse(cache.isFull());
		cache.put("3", "3");
		assertEquals(3, cache.size());
		assertTrue(cache.isFull());

		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		cache.put("4", "4");        // new element, cache is full, prune is invoked
		assertNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("4"));
		cache.put("1", "1");

		assertNull(cache.get("2"));
		assertNotNull(cache.get("3"));
		assertNotNull(cache.get("4"));
		assertNotNull(cache.get("1"));

		cache.clear();
		assertEquals(3, cache.capacity());
		assertEquals(0, cache.size());
	}

	@Test
	public void testCacheTime() {
		Cache<String, String> cache = new FIFOCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3", 50);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cache.put("4", "4");

		assertNotNull(cache.get("1"));
		assertNotNull(cache.get("2"));
		assertNull(cache.get("3"));
		assertNotNull(cache.get("4"));

	}

	@Test
	public void testCacheIterator() {
		Cache<String, String> cache = new FIFOCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3", 50);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Iterator<String> it = cache.iterator();
		int count = 0;
		while (it.hasNext()) {
			String s = it.next();
			if (s.equals("3")) {
				fail();
			}
			count++;
		}
		assertEquals(2, count);
	}

	@Test
	public void testCacheTime2() {
		Cache<String, String> cache = new FIFOCache<>(3, 50);
		cache.put("1", "1");
		cache.put("2", "2");
		assertEquals(2, cache.size());
		assertEquals(50, cache.timeout());

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(2, cache.prune());
		assertEquals(0, cache.size());
		assertTrue(cache.isEmpty());
	}

	@Test
	public void testPrune() {
		Cache<String, String> cache = new FIFOCache<>(3);
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3");

		assertEquals(1, cache.prune());
		assertEquals(2, cache.size());
	}

	@Test
	public void testOrder() {
		FIFOCache<String, Integer> fifoCache = new FIFOCache<>(3);
		fifoCache.put("1", Integer.valueOf(1));
		fifoCache.put("2", Integer.valueOf(2));
		fifoCache.put("3", Integer.valueOf(3));
		fifoCache.put("1", Integer.valueOf(1));
		fifoCache.put("1", Integer.valueOf(11));

		assertThat(2, equalTo(fifoCache.size()));

		assertThat(11, equalTo(fifoCache.get("1")));
		assertThat(3, equalTo(fifoCache.get("3")));

	}

}