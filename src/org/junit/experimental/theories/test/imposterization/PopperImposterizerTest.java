package org.junit.experimental.theories.test.imposterization;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.imposterization.PopperImposterizer;

public class PopperImposterizerTest {
	@Test
	public void canWrapImposterizedObjects() {
		List<?> list= new PopperImposterizer(null).imposterize(List.class);
		assertThat(new PopperImposterizer(null).imposterize(list.getClass()),
				notNullValue());
	}
}
