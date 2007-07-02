package org.junit.experimental.theories.test.matchers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.experimental.theories.matchers.api.StackTrace;

public class StackTraceTest {
	private static class ThingWithStackTrace {
		private StackTrace stackTrace;

		private ThingWithStackTrace() {
			stackTrace = StackTrace.create();
		}
	}

	private ThingWithStackTrace newThing() {
		return new ThingWithStackTrace();
	}

	@Test public void factoryMethodName() {
		assertThat(newThing().stackTrace.factoryMethodName(), is("newThing"));
	}

	@Test public void characterizeStackTrace() {
		StackTraceElement[] stackTrace = newThing().stackTrace.getElements();
		String methodNames = "";

		for (StackTraceElement element : stackTrace) {
			methodNames += element.getMethodName() + ":";
		}

		assertThat(methodNames, hasToString(containsString(":<init>:")));
	}
}
