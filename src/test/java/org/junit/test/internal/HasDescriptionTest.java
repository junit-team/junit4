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

public class HasDescriptionTest {
	private static final String NAME_OF_DESCRIPTION= "a description";

	private final Description description= createSuiteDescription(NAME_OF_DESCRIPTION);

	private final Description anotherDescription= createSuiteDescription("another description");

	private final RuntimeException exception= new RuntimeException();

	private final Failure failure= new Failure(description, exception);

	@Test
	public void matchWithMatcher() {
		assertThat(failure, new HasDescription(equalTo(description)));
	}

	@Test
	public void matchWithDescription() {
		assertThat(failure, new HasDescription(description));
	}

	@Test
	public void dontMatchWrongDescription() {
		assertThat(failure, not(new HasDescription(anotherDescription)));
	}

	@Test
	public void dontMatchNull() {
		assertThat(null, not(new HasDescription(description)));
	}

	@Test
	public void hasMeaningfulDescription() {
		HasDescription matcher= new HasDescription(description);
		StringDescription description= new StringDescription();
		matcher.describeTo(description);
		assertThat(description.toString(), is(equalTo("description is <"
				+ NAME_OF_DESCRIPTION + ">")));
	}
}
