package org.junit.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.matchers.Each;

public class EachTest {
	@Test
	public void eachDescription() {
		assertThat(Each.each(is("a")).toString(), is("each is \"a\""));
	}
}
