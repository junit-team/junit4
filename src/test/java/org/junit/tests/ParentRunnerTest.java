package org.junit.tests;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParentRunnerTest {
	public static String log= "";

	public static class FruitTest {
		@Test
		public void apple() {
			log+= "apple ";
		}

		@Test
		public void banana() {
			log+= "banana ";
		}
	}

	@Test
	public void useChildHarvester() throws InitializationError {
		log= "";
		ParentRunner<?> runner= new BlockJUnit4ClassRunner(FruitTest.class);
		runner.setScheduler(new RunnerScheduler() {
			public void schedule(Runnable childStatement) {
				log+= "before ";
				childStatement.run();
				log+= "after ";
			}

			public void finished() {
				log+= "afterAll ";
			}
		});

		runner.run(new RunNotifier());
		assertEquals("before apple after before banana after afterAll ", log);
	}
}
