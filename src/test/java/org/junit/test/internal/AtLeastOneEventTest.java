package org.junit.test.internal;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.runner.Description.createSuiteDescription;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.test.EventCollector;

public class AtLeastOneEventTest {
	private static final Description ARBITRARY_DESCRIPTION= createSuiteDescription("arbtirary name");

	private static final AssertionError ARBITRARY_EXCEPTION= new AssertionError(
			"arbitrary message");

	private static final Failure FIRST_FAILURE= new Failure(
			ARBITRARY_DESCRIPTION, ARBITRARY_EXCEPTION);

	private static final Failure SECOND_FAILURE= new Failure(
			ARBITRARY_DESCRIPTION, ARBITRARY_EXCEPTION);

	private final EventCollector collector= new EventCollector();

	@SuppressWarnings("unchecked")
	@Test
	public void matchTwoFailures() throws Exception {
		collector.testFailure(FIRST_FAILURE);
		collector.testFailure(SECOND_FAILURE);
		assertThat(
				collector,
				allOf(atLeastOneFailure(FIRST_FAILURE),
						atLeastOneFailure(SECOND_FAILURE)));
	}

	@Test
	public void dontMatchWrongFailure() throws Exception {
		collector.testFailure(SECOND_FAILURE);
		assertThat(collector, not(atLeastOneFailure(FIRST_FAILURE)));
	}

	@Test
	public void dontMatchNull() {
		assertThat(null, not(atLeastOneFailure(FIRST_FAILURE)));
	}

	@Test
	public void hasMeaningfulDescription() {
		AtLeastOnEvent<Failure> matcher= atLeastOneFailure(FIRST_FAILURE);
		StringDescription description= new StringDescription();
		matcher.describeTo(description);
		assertThat(
				description.toString().startsWith(
						"failure: <arbtirary name: arbitrary message>"),
				is(true));
	}

	private AtLeastOnEvent<Failure> atLeastOneFailure(Failure failure) {
		return new AtLeastOnEvent<Failure>(new ReadFailuresCommand(),
				equalTo(failure));
	}
}
