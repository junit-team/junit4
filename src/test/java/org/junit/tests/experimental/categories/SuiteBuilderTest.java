package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.CategoryFilter;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class SuiteBuilderTest {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RunnerFilter {
		public static interface Value {
			public abstract List<Runner> matchingRunners(
					List<Runner> allPossibleRunners);
		}
	}

	public static class Listed implements Classes.Value {
		private final Class<?>[] fClasses;

		public Listed(Class<?>... classes) {
			fClasses= classes;
		}

		public List<? extends Class<?>> get() {
			return Arrays.asList(fClasses);
		}
	}

	// Classes -> RunnerBuilder

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Classes {
		public static interface Value {
			public abstract Collection<? extends Class<?>> get();
		}
	}

	public static class SuiteBuilder extends Runner {
		private final TestClass fTestClass;

		private final Object fInstance;

		private final List<Runner> fRunners;

		public SuiteBuilder(Class<?> testClass) throws InitializationError {
			fTestClass= new TestClass(testClass);
			// TODO: extract complexity
			try {
				fInstance= fTestClass.getOnlyConstructor().newInstance();
			} catch (Exception e) {
				throw new InitializationError(e);
			}
			fRunners= computeRunners();
		}

		@Override
		public Description getDescription() {
			Description description= Description.createSuiteDescription(fTestClass.getJavaClass());
			for (Runner each : fRunners) {
				description.addChild(each.getDescription());
			}
			return description;
		}

		// TODO: require an instance?
		@Override
		public void run(RunNotifier notifier) {
			for (Runner each : fRunners)
				each.run(notifier);
		}

		private List<Runner> computeRunners() {
			List<Class<?>> allPossibleClasses= gatherClasses();
			List<Runner> allPossibleRunners= runnersForClasses(allPossibleClasses);
			return filterRunners(allPossibleRunners);
		}

		private List<Runner> filterRunners(List<Runner> allPossibleRunners) {
			List<Runner> result= allPossibleRunners;
			for (RunnerFilter.Value each : getFilters())
				result= each.matchingRunners(result);
			return result;
		}

		private List<RunnerFilter.Value> getFilters() {
			return fTestClass.getAnnotatedFieldValues(fInstance,
					RunnerFilter.class, RunnerFilter.Value.class);
		}

		private List<Runner> runnersForClasses(List<Class<?>> allPossibleClasses) {
			// TODO: cheating
			ArrayList<Runner> result= new ArrayList<Runner>();
			for (Class<?> each : allPossibleClasses) {
				try {
					result.add(new BlockJUnit4ClassRunner(each));
				} catch (InitializationError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}

		private List<Class<?>> gatherClasses() {
			ArrayList<Class<?>> result= new ArrayList<Class<?>>();
			List<Classes.Value> classeses= fTestClass.getAnnotatedFieldValues(
					fInstance, Classes.class, Classes.Value.class);
			for (Classes.Value each : classeses)
				result.addAll(each.get());
			return result;
		}
	}

	static class Yes {
	}

	static class No {
	}

	@Category(Yes.class)
	public static class Yes1 {
		@Test
		public void yes1() {
		}
	}

	@Category(Yes.class)
	public static class Yes2 {
		@Test
		public void yes2() {
		}
	}

	@Category(No.class)
	public static class No1 {
		@Test
		public void no1() {
		}
	}

	@RunWith(SuiteBuilder.class)
	public static class OnlyYesJustOne {
		@Classes
		public Listed classes= new Listed(Yes1.class, No1.class);
		
		@RunnerFilter
		public CategoryFilter filter= CategoryFilter.include(Yes.class);
	}

	@RunWith(SuiteBuilder.class)
	public static class OnlyYesMaybeTwo {
		@Classes
		public Listed classes= new Listed(Yes1.class, Yes2.class, No1.class);

		@RunnerFilter
		public CategoryFilter filter= CategoryFilter.include(Yes.class);
	}

	@RunWith(SuiteBuilder.class)
	public static class Everything {
		@Classes
		public Listed classes= new Listed(Yes1.class, Yes2.class, No1.class);
	}

	@RunWith(SuiteBuilder.class)
	public static class Nos {
		@Classes
		public Listed classes= new Listed(Yes1.class, Yes2.class, No1.class);

		@RunnerFilter
		public CategoryFilter filter= CategoryFilter.include(No.class);
	}

	@Test
	public void gatherClasses() throws InitializationError {
		assertEquals(2, new SuiteBuilder(OnlyYesJustOne.class).gatherClasses().size());
	}
	
	@Test public void suiteBuilderDescription() throws InitializationError {
		Description description= new SuiteBuilder(Nos.class).getDescription();
		assertEquals(1, description.getChildren().size());
		assertEquals(Nos.class.getName(), description.getDisplayName());
		assertEquals(No1.class.getName(), description.getChildren().get(0).getDisplayName());
	}

	@Test
	public void onlyRunOne() {
		Result result= new JUnitCore().run(OnlyYesJustOne.class);
		assertEquals(1, result.getRunCount());
		assertThat(testResult(OnlyYesJustOne.class), isSuccessful());
	}

	@Test
	public void runTwo() {
		Result result= new JUnitCore().run(OnlyYesMaybeTwo.class);
		assertEquals(2, result.getRunCount());
		assertThat(testResult(OnlyYesMaybeTwo.class), isSuccessful());
	}

	@Test
	public void runAllThree() {
		Result result= new JUnitCore().run(Everything.class);
		assertEquals(3, result.getRunCount());
		assertThat(testResult(Everything.class), isSuccessful());
	}

	@Test
	public void runOneNo() {
		Result result= new JUnitCore().run(Nos.class);
		assertEquals(1, result.getRunCount());
		assertThat(testResult(Nos.class), isSuccessful());
	}

	@Test
	public void matchingRunnersOnCategories() throws InitializationError {
		Runner blockJUnit4ClassRunner= new BlockJUnit4ClassRunner(Yes1.class);
		assertEquals(1, CategoryFilter.include(Yes.class).matchingRunners(
				Arrays.asList(blockJUnit4ClassRunner)).size());
	}
}
