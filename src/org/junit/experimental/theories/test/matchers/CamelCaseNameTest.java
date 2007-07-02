package org.junit.experimental.theories.test.matchers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.experimental.theories.matchers.api.CamelCaseName;


public class CamelCaseNameTest {
	@Test
	public void basicParsing() {
		assertThat(new CamelCaseName("hasAFreezer").asNaturalLanguage(),
				is("has a freezer"));
	}
}
