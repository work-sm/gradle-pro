package com.sam.demo.nerver.common.cache;

import com.sam.demo.nerver.common.cache.support.FileLFUCache;
import com.sam.demo.nerver.common.cache.support.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FileLFUCacheTest {

	private File tempFolder = new File(System.getProperty("java.io.tmpdir"));

	private File file(String fileName, int size) throws IOException {
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) i;
		}

		File file = new File(tempFolder, fileName);
		file.deleteOnExit();

		FileUtil.writeBytes(file, bytes);

		return file;
	}

	@Test
	public void testCache() throws IOException {
		FileLFUCache cache = new FileLFUCache(25);

		assertEquals(25, cache.capacity());
		assertEquals(12, cache.maxFileSize());

		File a = file("a", 10);
		File b = file("b", 9);
		File c = file("c", 7);

		cache.getFileBytes(a);
		cache.getFileBytes(a);
		cache.getFileBytes(a);
		cache.getFileBytes(b);

		assertEquals(1, cache.getCachedFilesCount());
		assertEquals(9, cache.getUsedSize());

		cache.getFileBytes(c);        // b is out, a(2), c(1)

		assertEquals(1, cache.getCachedFilesCount());
		assertEquals(7, cache.getUsedSize());

		cache.getFileBytes(c);
		cache.getFileBytes(c);
		cache.getFileBytes(c);

		cache.getFileBytes(b);        // a is out

		assertEquals(1, cache.getCachedFilesCount());
		assertEquals(9, cache.getUsedSize());
	}
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.io.tmpdir"));
	}
}