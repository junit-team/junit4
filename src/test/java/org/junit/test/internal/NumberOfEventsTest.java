package org.junit.test.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.test.EventCollector;

public class NumberOfEventsTest {
	private static final Failure ARBITRARY_FAILURE= new Failure(null, null);

	private final EventCollector collector= new EventCollector();

	@Test
	public void matchOneFailure() throws Exception {
		collector.testFailure(ARBITRARY_FAILURE);
		assertThat(collector, numberOfFailures(1));
	}

	@Test
	public void dontMatchWrongNumberOfFailures() throws Exception {
		assertThat(collector, not(numberOfFailures(1)));
	}

	@Test
	public void dontMatchNull() {
		assertThat(null, not(numberOfFailures(0)));
	}

	@Test
	public void hasMeaningfulDescription() {
		NumberOfEvents<Failure> matcher= numberOfFailures(2);
		StringDescription description= new StringDescription();
		matcher.describeTo(description);
		assertThat(description.toString(), is(equalTo("2 failures")));
	}

	private NumberOfEvents<Failure> numberOfFailures(int numberOfFailures) {
		return new NumberOfEvents<Failure>(new ReadFailuresCommand(),
				numberOfFailures);
	}
}
