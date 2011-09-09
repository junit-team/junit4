package org.junit.tests.experimental.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

class LoggingTestWatcher extends TestWatcher {
	private final StringBuilder log;

	LoggingTestWatcher(StringBuilder log) {
		this.log= log;
	}

	@Override
	protected void succeeded(Description description) {
		log.append("succeeded ");
	}

	@Override
	protected void failed(Throwable e, Description description) {
		log.append("failed ");
	}

	@Override
	protected void starting(Description description) {
		log.append("starting ");
	}

	@Override
	protected void finished(Description description) {
		log.append("finished ");
	}
}