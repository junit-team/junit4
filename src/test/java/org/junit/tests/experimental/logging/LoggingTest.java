package org.junit.tests.experimental.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

//TODO(logging) log success/fail
//TODO(logging) log class start/finish
//TODO(logging) log timing of single tests

public class LoggingTest {
	public class TestLog {

		private int runCount= 0;
		private int failureCount= 0;

		public int getRunCount() {
			return runCount;
		}

		public void setRunCount(int count) {
			runCount= count;
		}

		public int getFailureCount() {
			return failureCount;
		}

		public void setFailureCount(int count) {
			this.failureCount= count;
		}

	}

	public class LoggingListener extends RunListener {
		private StringBuffer writer= new StringBuffer();
		@Override
		public void testStarted(Description description) throws Exception {
			writer.append("t");
		}
		
		@Override
		public void testFailure(Failure failure) throws Exception {
			writer.append("f");
		}

		public String getLogContents() {
			return writer.toString();
		}

	}

	public static class Example {
		@Test public void uno() {}
		@Test public void due() {}
	}
	
	@Test public void logSuccess() {
		JUnitCore core= new JUnitCore();
		LoggingListener listener=addLogging(core);
		core.run(Example.class);
		String log= listener.getLogContents();
		TestLog result = parse(log);
		assertEquals(2, result.getRunCount());
		assertEquals(0, result.getFailureCount());
		// some assertions
	}

	public static class FailingExample {
		@Test public void uno() {}
		@Test public void due() {
			fail();
		}
	}
	
	@Test public void logFailure() {
		JUnitCore core= new JUnitCore();
		LoggingListener listener=addLogging(core);
		core.run(FailingExample.class);
		String log= listener.getLogContents();
		TestLog result = parse(log);
		assertEquals(2, result.getRunCount());
		assertEquals(1, result.getFailureCount());
		// some assertions
	}

	private TestLog parse(String log) {
		TestLog result= new TestLog();
		result.setRunCount(count(log, 't'));
		result.setFailureCount(count(log, 'f'));
		return result;
	}

	private int count(String log, char pattern) {
		int count= 0;
		for (int i = 0; i < log.length(); i++)
			if (log.charAt(i) == pattern)
				count++;
		return count;
	}

	private LoggingListener addLogging(JUnitCore core) {
		LoggingListener listener= new LoggingListener();
		core.addListener(listener);
		return listener;
	}
}
