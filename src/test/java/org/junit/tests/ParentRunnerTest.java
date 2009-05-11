package org.junit.tests;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;

// TODO (May 11, 2009 2:42:38 PM): move package

public class ParentRunnerTest {
	private static StringBuffer log= new StringBuffer();

	public static class FruitTests {
		@Test
		public void apple() {
			log.append("apple ");
		}

		@Test
		public void banana() {
			log.append("banana ");
		}

		@Test
		public void pear() {
			log.append("pear ");
		}
	}

	@Test
	public void installDecorator() throws Throwable {
		log.setLength(0);
		ParentRunner<FrameworkMethod> runner= (BlockJUnit4ClassRunner) new AllDefaultPossibilitiesBuilder(
				true).runnerForClass(FruitTests.class);
		// TODO (May 11, 2009 2:58:11 PM): DUP?
		runner.sort(new Sorter(new Comparator<Description>() {
			public int compare(Description o1, Description o2) {
				return o1.toString().compareTo(o2.toString());
			}
		}));
		runner.installDecorator(new ParentRunner.Decorator() {
			public void runChild(SafeStatement statement) {
				log.append("before ");
				statement.execute();
				log.append("after ");
			}

			public void runAll(SafeStatement statement) {
				log.append("beforeAll ");
				statement.execute();
				log.append("afterAll ");
			}
		});

		runner.run(new RunNotifier());
		assertEquals(
				"beforeAll before apple after before banana after before pear after afterAll ",
				log.toString());
	}
}
