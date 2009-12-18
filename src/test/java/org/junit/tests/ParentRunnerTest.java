package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
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
	
	@RunWith(Suite.class)
	@SuiteClasses({FruitTest.class})
	public static class FruitSuite {
		
	}
	
	@Test public void saneBehaviorWhenNoTestsShouldRun() {
		Filter nothing= new Filter() {			
			@Override
			public boolean shouldRun(Description description) {
				return false;
			}
			
			@Override
			public String describe() {
				return "nothing";
			}
		};
		assertNotNull(Request.aClass(FruitSuite.class).filterWith(nothing).getRunner());
	}
}
