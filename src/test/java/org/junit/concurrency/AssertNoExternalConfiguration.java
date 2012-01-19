package org.junit.concurrency;

import org.junit.Test;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import static org.junit.Assert.*;

import static de.tarent.advproxy.test.concurrency.ConcurrencyConfiguration.*;

public class AssertNoExternalConfiguration {
	@Test
	public void assertNoExternalConfiguration() {
		assertNull(getProperty(TIMES_DEFAULT_KEY));
		assertNull(getProperty(TIMES_MULTIPLICATOR_KEY));
		assertNull(getProperty(TIMES_MIN_KEY));
		assertNull(getProperty(TIMES_MAX_KEY));
		assertNull(getProperty(THREADS_DEFAULT_KEY));
		assertNull(getProperty(THREADS_MULTIPLICATOR_KEY));
		assertNull(getProperty(THREADS_MIN_KEY));
		assertNull(getProperty(THREADS_MAX_KEY));
		
		assertNull(getenv(TIMES_DEFAULT_KEY));
		assertNull(getenv(TIMES_MULTIPLICATOR_KEY));
		assertNull(getenv(TIMES_MIN_KEY));
		assertNull(getenv(TIMES_MAX_KEY));
		assertNull(getenv(THREADS_DEFAULT_KEY));
		assertNull(getenv(THREADS_MULTIPLICATOR_KEY));
		assertNull(getenv(THREADS_MIN_KEY));
		assertNull(getenv(THREADS_MAX_KEY));
	}
}
