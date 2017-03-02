package org.junit.concurrency;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ConcurrencyJunitRunner.class)
@Concurrency(times = 100000, parallelThreads = 1000)
public class HashMapTest {
	private static Map<String, String> testMap;
	
	@BeforeClass
	public static void setUp() {
		testMap = new HashMap<String, String>();
	}
	
	@AfterClass
	public static void tearDown() {
		testMap = null;
	}
	
	@Test
	@Concurrency(expectAtLeast = ConcurrentModificationException.class)
	public void illegalConcurrentAccessWithDifferentKeys() {
		String key = Thread.currentThread().getName();
		String value = System.currentTimeMillis() + "-" + System.nanoTime();
		modifyAndIterate(key, value);
	}
	
	@Test
	@Concurrency
	public void legalConcurrentAccessWithSingleKeyTest() {
		String key = "key";
		String value = System.currentTimeMillis() + "-" + System.nanoTime();
		modifyAndIterate(key, value);
	}
	
	private void modifyAndIterate(String key, String value) {
		testMap.put(key, value);
		Iterator<Map.Entry<String, String>> it = testMap.entrySet().iterator();
		while (it.hasNext()) {
			it.next();
		}
	}
}
