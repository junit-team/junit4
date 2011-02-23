package org.junit.rules;

import org.junit.ClassRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Runs a collection of rules on a statement.
 */
public class RunRules extends Statement {
	private final Statement statement;

	/**
	 * 
	 * @param target target test instance, or null when this rule is being applied as a {@link ClassRule}
	 */
	public RunRules(Statement base, Iterable<TestRule> rules, Description description, Object target) {
		statement= applyAll(base, rules, description, target);
	}
	
	@Override
	public void evaluate() throws Throwable {
		statement.evaluate();
	}

	private static Statement applyAll(Statement result, Iterable<TestRule> rules,
			Description description, Object target) {
		for (TestRule each : rules)
			result= each.apply(result, description, target);
		return result;
	}
}
