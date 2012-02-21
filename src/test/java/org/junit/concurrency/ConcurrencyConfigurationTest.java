package org.junit.concurrency;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import static java.lang.System.clearProperty;
import static java.lang.System.setProperty;
import static org.junit.Assert.*;

import static org.junit.concurrency.ConcurrencyConfiguration.*;

public class ConcurrencyConfigurationTest {
	private Class<?> clazz;
	private Method method;
	private ConcurrencyConfiguration configuration;

	@Before
	public void resetProperties() {
		clearProperty(TIMES_DEFAULT_KEY);
		clearProperty(TIMES_MULTIPLICATOR_KEY);
		clearProperty(TIMES_MIN_KEY);
		clearProperty(TIMES_MAX_KEY);
		clearProperty(THREADS_DEFAULT_KEY);
		clearProperty(THREADS_MULTIPLICATOR_KEY);
		clearProperty(THREADS_MIN_KEY);
		clearProperty(THREADS_MAX_KEY);
	}

	@Test
	public void testDefaultWithoutConcurrenyAnnotation() {
		getConfiguration("TestDefaultWithoutConcurrencyAnnotation");
		assertEquals(TIMES_DEFAULT, getMethodTimes("testDefault"));
		assertEquals(THREADS_DEFAULT, getMethodThreads("testDefault"));
	}

	@Test
	public void testDefaultWithConcurrencyAnnotationAtClass() {
		getConfiguration("TestDefaultWithConcurrencyAnnotation");
		assertEquals(TIMES_DEFAULT, getMethodTimes("testAnnotationAtClass"));
		assertEquals(THREADS_DEFAULT, getMethodThreads("testAnnotationAtClass"));
	}

	@Test
	public void testDefaultWithConcurrencyAnnotationAtMethod() {
		getConfiguration("TestDefaultWithConcurrencyAnnotation");
		assertEquals(TIMES_DEFAULT, getMethodTimes("testAnnotationAtMethod"));
		assertEquals(THREADS_DEFAULT, getMethodThreads("testAnnotationAtMethod"));
	}

	@Test
	public void testOverride() {
		getConfiguration("TestOverride");
		assertEquals(11, getMethodTimes("testValueFromClass1"));
		assertEquals(11, getMethodThreads("testValueFromClass1"));
		assertEquals(11, getMethodTimes("testValueFromClass2"));
		assertEquals(11, getMethodThreads("testValueFromClass2"));
		assertEquals(22, getMethodTimes("testValueFromMethod"));
		assertEquals(22, getMethodThreads("testValueFromMethod"));
	}

	@Test
	public void testDefaultValuesWithMinimum() {
		setProperty(TIMES_MIN_KEY, "20");
		setProperty(THREADS_MIN_KEY, "20");
		
		getConfiguration("TestDefaultWithoutConcurrencyAnnotation");
		assertEquals(20, getMethodTimes("testDefault"));
		assertEquals(20, getMethodThreads("testDefault"));
	}

	@Test
	public void testOverrideWithMaximum() {
		setProperty(TIMES_MAX_KEY, "15");
		setProperty(THREADS_MAX_KEY, "15");
		
		getConfiguration("TestOverride");
		assertEquals(11, getMethodTimes("testValueFromClass1"));
		assertEquals(11, getMethodThreads("testValueFromClass1"));
		assertEquals(11, getMethodTimes("testValueFromClass2"));
		assertEquals(11, getMethodThreads("testValueFromClass2"));
		assertEquals(15, getMethodTimes("testValueFromMethod"));
		assertEquals(15, getMethodThreads("testValueFromMethod"));
	}

	@Test
	public void testInheritance() {
		getConfiguration("TestInheritance");
		assertEquals(11, getMethodTimes("testValueFromClass1"));
		assertEquals(11, getMethodThreads("testValueFromClass1"));
		assertEquals(11, getMethodTimes("testValueFromClass2"));
		assertEquals(11, getMethodThreads("testValueFromClass2"));
		assertEquals(22, getMethodTimes("testValueFromMethod"));
		assertEquals(22, getMethodThreads("testValueFromMethod"));
	}
	
	private ConcurrencyConfiguration getConfiguration(String name) {
		try {
			clazz = Class.forName(getClass().getName() + "$" + name);
			configuration = new ConcurrencyConfiguration(clazz);
			return configuration;
		} catch (Exception e) {
			throw new AssertionError(e.toString());
		}
	}

	private Method getMethod(String name) {
		try {
			method = clazz.getMethod(name);
			return method;
		} catch (Exception e) {
			throw new AssertionError(e.toString());
		}
	}

	private int getMethodTimes(String name) {
		return configuration.getMethodTimes(getMethod(name));
	}

	private int getMethodThreads(String name) {
		return configuration.getMethodThreads(getMethod(name));
	}

	public static class TestDefaultWithoutConcurrencyAnnotation {
		public void testDefault() {
		}
	}

	@Concurrency
	public static class TestDefaultWithConcurrencyAnnotation {
		public void testAnnotationAtClass() {
		}
		
		@Concurrency
		public void testAnnotationAtMethod() {
		}
	}

	@Concurrency(times = 11, parallelThreads = 11)
	public static class TestOverride {
		public void testValueFromClass1() {
		}

		@Concurrency
		public void testValueFromClass2() {
		}

		@Concurrency(times = 22, parallelThreads = 22)
		public void testValueFromMethod() {
		}
	}
	
	public static class TestInheritance extends TestOverride {
	}
}
