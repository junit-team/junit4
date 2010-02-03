package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class CategoriesMark2Test {
	public static class CategoryFilter2 extends Filter2 {
		private final Class<?> fIncluded;

		public CategoryFilter2(Class<?> included) {
			fIncluded= included;
		}

		public static CategoryFilter2 include(Class<?> included) {
			return new CategoryFilter2(included);
		}

		@Override
		public List<Runner> matchingRunners(
				List<? extends Runner> allPossibleRunners) {
			ArrayList<Runner> result= new ArrayList<Runner>();
			for (Runner eachRunner : allPossibleRunners) {
				Collection<Annotation> annotations= eachRunner.getDescription().getAnnotations();
				// TODO: extract method
				for (Annotation eachAnnotation : annotations) {
					if (eachAnnotation.annotationType().equals(Category.class)) {
						Category category = (Category) eachAnnotation;
						Class<?>[] categories= category.value();
						if (Arrays.asList(categories).contains(fIncluded))
							result.add(eachRunner);
					}
				}
			}

			// TODO Auto-generated method stub
			return result;
		}
	}

	public static abstract class Filter2 {
		public abstract List<Runner> matchingRunners(
				List<? extends Runner> allPossibleRunners);
	}

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

	public static abstract class Classes {

		public abstract Collection<? extends Class<?>> get();

	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface SuiteClasses2 {

	}

	public static class Suite2 extends Runner {
		private final Class<?> fTestClass;

		public Suite2(Class<?> testClass) {
			fTestClass= testClass;
		}

		@Override
		public Description getDescription() {
			// TODO Auto-generated method stub
			return null;
		}

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
			// TODO: shouldn't do this twice
			ArrayList<Filter2> result= new ArrayList<Filter2>();
			TestClass testClass= new TestClass(fTestClass);
			Object target= createInstance(testClass);
			List<FrameworkField> fields= testClass
					.getAnnotatedFields(FilterWith.class);
			for (FrameworkField each : fields)
				result.add((Filter2) getValue(each, target));

			// TODO Auto-generated method stub
			return result;
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
			// TODO: Something's wrong with the naming here.
			TestClass testClass= new TestClass(fTestClass);
			// TODO: where _should_ we be instantiating this?
			Object target= createInstance(testClass);
			List<FrameworkField> fields= testClass
					.getAnnotatedFields(SuiteClasses2.class);
			for (FrameworkField each : fields) {
				Classes classes= (Classes) getValue(each, target);
				// TODO: naming?
				result.addAll(classes.get());
			}
			return result;
		}

		private Object getValue(FrameworkField each, Object target) {
			// TODO: we must do this for rules, right?
			try {
				return each.get(target);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}

		private Object createInstance(TestClass testClass) {
			try {
				return testClass.getOnlyConstructor().newInstance();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
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
		public Filter2 filter= CategoryFilter2.include(Yes.class);
	}

	@RunWith(Suite2.class)
	public static class OnlyYesMaybeTwo {
		@SuiteClasses2
		public Classes classes= new Listed(Yes1.class, Yes2.class, No1.class);

		@FilterWith
		public Filter2 filter= CategoryFilter2.include(Yes.class);
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
		public Filter2 filter= CategoryFilter2.include(No.class);
	}

	@Test
	public void gatherClasses() {
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
	
	@Test public void runOneNo() {
		Result result= new JUnitCore().run(Nos.class);
		assertEquals(1, result.getRunCount());
		assertThat(testResult(Nos.class), isSuccessful());		
	}

	@Test
	public void matchingRunnersOnCategories() throws InitializationError {
		assertEquals(1, CategoryFilter2.include(Yes.class).matchingRunners(
				Arrays.asList(new BlockJUnit4ClassRunner(Yes1.class))).size());
	}
}
