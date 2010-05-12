/**
 * 
 */
package org.junit.experimental.runners;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class SuiteBuilder extends Runner {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RunnerFilter {
		public static interface Value {
			public abstract List<Runner> matchingRunners(
					List<Runner> allPossibleRunners);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Classes {
		public static interface Value {
			public abstract Collection<? extends Class<?>> get();
		}
	}

	private final TestClass fTestClass;
	private final Object fInstance;
	private final List<Runner> fRunners;

	public SuiteBuilder(Class<?> testClass) throws InitializationError {
		fTestClass= new TestClass(testClass);
		fInstance = createInstance();
		fRunners= computeRunners();
	}

	private Object createInstance() throws InitializationError {
		try {
			return fTestClass.getOnlyConstructor().newInstance();
		} catch (Exception e) {
			throw new InitializationError(e);
		}
	}

	@Override
	public Description getDescription() {
		Description description= Description.createSuiteDescription(fTestClass.getJavaClass());
		for (Runner each : fRunners) {
			description.addChild(each.getDescription());
		}
		return description;
	}

	@Override
	public void run(RunNotifier notifier) {
		for (Runner each : fRunners)
			each.run(notifier);
	}

	private List<Runner> computeRunners() throws InitializationError {
		List<Class<?>> allPossibleClasses= gatherClasses();
		List<Runner> allPossibleRunners= runnersForClasses(allPossibleClasses);
		return filterRunners(allPossibleRunners);
	}

	private List<Runner> filterRunners(List<Runner> allPossibleRunners) {
		List<Runner> result= allPossibleRunners;
		for (SuiteBuilder.RunnerFilter.Value each : getFilters())
			result= each.matchingRunners(result);
		return result;
	}

	private List<SuiteBuilder.RunnerFilter.Value> getFilters() {
		return fTestClass.getAnnotatedFieldValues(fInstance,
				SuiteBuilder.RunnerFilter.class, SuiteBuilder.RunnerFilter.Value.class);
	}

	private List<Runner> runnersForClasses(List<Class<?>> allPossibleClasses) throws InitializationError {
		return new AllDefaultPossibilitiesBuilder(true).runners(fTestClass
				.getJavaClass(), allPossibleClasses);
	}

	public List<Class<?>> gatherClasses() {
		ArrayList<Class<?>> result= new ArrayList<Class<?>>();
		List<SuiteBuilder.Classes.Value> classeses= fTestClass.getAnnotatedFieldValues(
				fInstance, SuiteBuilder.Classes.class, SuiteBuilder.Classes.Value.class);
		for (SuiteBuilder.Classes.Value each : classeses)
			result.addAll(each.get());
		return result;
	}
}