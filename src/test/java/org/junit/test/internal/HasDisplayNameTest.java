package org.junit.test.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.runner.Description.createSuiteDescription;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.runner.Description;

public class HasDisplayNameTest {
	private static final String DUMMY_NAME= "dummy name";

	private final Description dummyDescription= createSuiteDescription(DUMMY_NAME);

	@Test
	public void matchWithMatcher() {
		assertThat(dummyDescription, new HasDisplayName(equalTo(DUMMY_NAME)));
	}

	@Test
	public void matchWithString() {
		assertThat(dummyDescription, new HasDisplayName(DUMMY_NAME));
	}

	@Test
	public void dontMatchWrongName() {
		assertThat(dummyDescription, not(new HasDisplayName("wrong name")));
	}

	@Test
	public void dontMatchNull() {
		assertThat(null, not(new HasDisplayName("a name")));
	}

	@Test
	public void hasMeaningfulDescription() {
		HasDisplayName matcher= new HasDisplayName("a name");
		StringDescription description= new StringDescription();
		matcher.describeTo(description);
		assertThat(description.toString(),
				is(equalTo("display name is \"a name\"")));
	}
}
