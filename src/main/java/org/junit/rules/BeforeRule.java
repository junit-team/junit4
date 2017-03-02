package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class BeforeRule implements TestRule {

	private final FrameworkMethod before;

	private final Object testInstance;

	public BeforeRule(FrameworkMethod before, Object testInstance) {
		this.before= before;
		this.testInstance= testInstance;
	}

	public Statement apply(final Statement base, Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				before.invokeExplosively(testInstance);
				base.evaluate();
			}
		};
	}
}