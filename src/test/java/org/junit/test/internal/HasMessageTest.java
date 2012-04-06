package org.junit.test.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.runner.Description.createSuiteDescription;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class HasMessageTest {
	private static final String DUMMY_MESSAGE= "dummy message";

	private final Description description= createSuiteDescription("a description");

	private final RuntimeException exception= new RuntimeException(
			DUMMY_MESSAGE);

	private final Failure failure= new Failure(description, exception);

	@Test
	public void matchWithMatcher() {
		assertThat(failure, new HasMessage(equalTo(DUMMY_MESSAGE)));
	}

	@Test
	public void matchWithString() {
		assertThat(failure, new HasMessage(DUMMY_MESSAGE));
	}

	@Test
	public void dontMatchWrongName() {
		assertThat(failure, not(new HasMessage("wrong name")));
	}

	@Test
	public void dontMatchNull() {
		assertThat(null, not(new HasMessage("a name")));
	}

	@Test
	public void hasMeaningfulDescription() {
		HasMessage matcher= new HasMessage("a message");
		StringDescription description= new StringDescription();
		matcher.describeTo(description);
		assertThat(description.toString(),
				is(equalTo("message is \"a message\"")));
	}
}
