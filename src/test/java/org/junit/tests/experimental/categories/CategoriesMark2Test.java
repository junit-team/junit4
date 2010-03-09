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
import org.junit.experimental.categories.Filter2;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class CategoriesMark2Test {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FilterWith {

	}

	public static class Listed extends Classes {
		private final Class<?>[] fClasses;

		public Listed(Class<?>... classes) {
			fClasses= classes;
		}

		@Override
		public List<? extends Class<?>> get() {
			return Arrays.asList(fClasses);
		}
	}

	// Classes -> RunnerBuilder

	public static abstract class Classes {

		public abstract Collection<? extends Class<?>> get();

	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface SuiteClasses2 {

	}

	public static class Suite2 extends Runner {
		private final TestClass fTestClass;

		private final Object fInstance;

		public Suite2(Class<?> testClass) throws InitializationError {
			fTestClass= new TestClass(testClass);
			try {
				fInstance= fTestClass.getOnlyConstructor().newInstance();
			} catch (Exception e) {
				throw new InitializationError(e);
			}
		}

		@Override
		public Description getDescription() {
			// TODO Auto-generated method stub
			return null;
		}

		// TODO: require an instance?
		@Override
		public void run(RunNotifier notifier) {
			List<Class<?>> allPossibleClasses= gatherClasses();
			List<Runner> allPossibleRunners= runnersForClasses(allPossibleClasses);
			List<Runner> valid= filterRunners(allPossibleRunners);
			for (Runner each : valid)
				each.run(notifier);
		}

		private List<Runner> filterRunners(List<Runner> allPossibleRunners) {
			List<Runner> result= allPossibleRunners;
			List<Filter2> filters= getFilters();
			for (Filter2 each : filters)
				result= each.matchingRunners(result);
			return result;
		}

		private List<Filter2> getFilters() {
			return fTestClass.getAnnotatedFieldValues(fInstance,
					FilterWith.class, Filter2.class);
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
			List<Classes> classeses= fTestClass.getAnnotatedFieldValues(
					fInstance, SuiteClasses2.class, Classes.class);
			for (Classes each : classeses)
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

	@RunWith(Suite2.class)
	public static class OnlyYesJustOne {
		@SuiteClasses2
		public Classes classes= new Listed(Yes1.class, No1.class);

		@FilterWith
		public Filter2 filter= CategoryFilter.include(Yes.class);
	}

	@RunWith(Suite2.class)
	public static class OnlyYesMaybeTwo {
		@SuiteClasses2
		public Classes classes= new Listed(Yes1.class, Yes2.class, No1.class);

		@FilterWith
		public Filter2 filter= CategoryFilter.include(Yes.class);
	}

	@RunWith(Suite2.class)
	public static class Everything {
		@SuiteClasses2
		public Classes classes= new Listed(Yes1.class, Yes2.class, No1.class);
	}

	@RunWith(Suite2.class)
	public static class Nos {
		@SuiteClasses2
		public Classes classes= new Listed(Yes1.class, Yes2.class, No1.class);

		@FilterWith
		public Filter2 filter= CategoryFilter.include(No.class);
	}

	@Test
	public void gatherClasses() throws InitializationError {
		assertEquals(2, new Suite2(OnlyYesJustOne.class).gatherClasses().size());
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
