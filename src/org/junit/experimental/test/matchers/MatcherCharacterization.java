package org.junit.experimental.test.matchers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class MatcherCharacterization {
	@Test
	public void findCamelCase() {
		Pattern pattern= Pattern.compile("([A-Z]?[a-z]*)");
		String methodName= "hasAFreezer";
		Matcher matcher= pattern.matcher(methodName);
		assertThat(matcher.find(), is(true));
	}
}
