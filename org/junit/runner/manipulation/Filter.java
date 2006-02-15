package org.junit.runner.manipulation;

import org.junit.runner.Description;
import org.junit.runner.Runner;

public abstract class Filter {
	public static Filter ALL= new Filter() {
		@Override
		public boolean shouldRun(Description description) {
			return true;
		}

		@Override
		public String describe() {
			return "all tests";
		}
	};

	public abstract boolean shouldRun(Description description);

	public Runner apply(Runner runner) throws NoTestsRemainException {
		if (runner instanceof Filterable) {
			Filterable filterable= (Filterable)runner;
			filterable.filter(this);
		}
		return runner;
	}

	public abstract String describe();
}
