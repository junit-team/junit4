package org.junit.runners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Runner;

public abstract class RunnerBuilder {
	private final Set<Class<?>> parents = new HashSet<Class<?>>();

	public abstract Runner runnerForClass(Class<?> testClass) throws Throwable;

	Class<?> addParent(Class<?> parent) throws InitializationError {
		if (!parents.add(parent))
			throw new InitializationError(String.format("class '%s' (possibly indirectly) contains itself as a SuiteClass", parent.getName()));
		return parent;
	}
	
	void removeParent(Class<?> klass) {
		parents.remove(klass);
	}

	List<Runner> runners(Class<?>[] children) {
		ArrayList<Runner> runners= new ArrayList<Runner>();
		for (Class<?> each : children) {
			Runner childRunner= safeRunnerForClass(each);
			if (childRunner != null)
				runners.add(childRunner);
		}
		return runners;
	}

	public Runner safeRunnerForClass(Class<?> testClass) {
		try {
			return runnerForClass(testClass);
		} catch (Throwable e) {
			return new ErrorReportingRunner(testClass, e);
		}
	}

	List<Runner> runners(Class<?> parent, Class<?>[] children) throws InitializationError {
		addParent(parent);
		
		try {
			return runners(children);
		} finally {
			removeParent(parent);
		}
	}
}
