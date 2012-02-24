package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class BlockJUnit4ClassRunnerTest {
	public static class OuterClass {
		public class Enclosed {
			@Test
			public void test() {
			}
		}
	}

	@Test
	public void detectNonStaticEnclosedClass() throws Exception {
		try {
			new BlockJUnit4ClassRunner(OuterClass.Enclosed.class);
		} catch (InitializationError e) {
			List<Throwable> causes= e.getCauses();
			assertEquals("Wrong number of causes.", 1, causes.size());
			assertEquals(
					"Wrong exception.",
					"The inner class org.junit.tests.running.classes.BlockJUnit4ClassRunnerTest$OuterClass$Enclosed is not static.",
					causes.get(0).getMessage());
		}
	}
}