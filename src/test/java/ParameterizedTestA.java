import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedTestA {
	// TODO: put this somewhere better
	public ParameterizedTestA(String a) {
	}

	@Parameters
	public static Collection<String[]> getParameters() {
		return Collections.singletonList(new String[] { "a" });
	}

	@Test
	public void testSomething() {
		Assert.assertTrue(true);
	}
}