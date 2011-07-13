package org.junit.tests.experimental.rules.tests;

import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

@SuppressWarnings("deprecation")
public class TestWithMethodRule {
	@Rule
	public MethodRule temporaryFolder = new MethodRule(){
		public Statement apply(Statement base, FrameworkMethod method,
				Object target) {
			return null;
		}};
}