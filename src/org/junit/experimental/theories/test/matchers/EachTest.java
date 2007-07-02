package org.junit.experimental.theories.test.matchers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.theories.matchers.api.Each;

public class EachTest {
	@Test
	public void eachDescription() {
		assertThat(Each.each(Matchers.is("a")).toString(), is("each is \"a\""));
	}
}
