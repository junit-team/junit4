package org.junit.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

public class AfterRule implements TestRule {

	private final FrameworkMethod after;

	private final Object testInstance;

	public AfterRule(FrameworkMethod after, Object testInstance) {
		this.after= after;
		this.testInstance= testInstance;
	}

	public Statement apply(final Statement base, Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				List<Throwable> errors= new ArrayList<Throwable>();
				try {
					base.evaluate();
				} catch (Throwable e) {
					errors.add(e);
				} finally {
					try {
						after.invokeExplosively(testInstance);
					} catch (Throwable e) {
						errors.add(e);
					}
					MultipleFailureException.assertEmpty(errors);
				}
			}
		};
	}
}
