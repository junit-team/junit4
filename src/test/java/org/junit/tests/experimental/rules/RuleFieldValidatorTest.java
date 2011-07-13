package org.junit.tests.experimental.rules;

import static org.junit.Assert.*;
import static org.junit.rules.RuleFieldValidator.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.model.TestClass;
import org.junit.tests.experimental.rules.tests.TestWithArbitraryObjectWithRuleAnnotation;
import org.junit.tests.experimental.rules.tests.TestWithMethodRule;
import org.junit.tests.experimental.rules.tests.TestWithNonStaticClassRule;
import org.junit.tests.experimental.rules.tests.TestWithNonStaticTestRule;
import org.junit.tests.experimental.rules.tests.TestWithNullClassRule;
import org.junit.tests.experimental.rules.tests.TestWithProtectedClassRule;

public class RuleFieldValidatorTest {
	private final List<Throwable> errors = new ArrayList<Throwable>();
	
	@Test
	public void rejectProtectedClassRule() {
		TestClass target= new TestClass(TestWithProtectedClassRule.class);
		CLASS_RULE_VALIDATOR.validate(target, errors);
		assertOneErrorWithMessage("The TestRule 'temporaryFolder' must be public.");
	}

	@Test
	public void rejectNonStaticClassRule() {
		TestClass target= new TestClass(TestWithNonStaticClassRule.class);
		CLASS_RULE_VALIDATOR.validate(target, errors);
		assertOneErrorWithMessage("The TestRule 'temporaryFolder' must be static.");
	}

	@Test
	public void acceptNonStaticTestRule() {
		TestClass target= new TestClass(TestWithNonStaticTestRule.class);
		RULE_VALIDATOR.validate(target, errors);
		assertNumberOfErrors(0);
	}

	@Test
	public void acceptMethodRule() throws Exception {
		TestClass target= new TestClass(TestWithMethodRule.class);
		RULE_VALIDATOR.validate(target, errors);
		assertNumberOfErrors(0);
	}

	@Test
	public void rejectNullClassRule() throws Exception {
		TestClass target= new TestClass(TestWithNullClassRule.class);
		CLASS_RULE_VALIDATOR.validate(target, errors);
		assertOneErrorWithMessage("The TestRule 'RULE' must not be null.");
	}

	@Test
	public void rejectArbitraryObjectWithRuleAnnotation() throws Exception {
		TestClass target= new TestClass(TestWithArbitraryObjectWithRuleAnnotation.class);
		RULE_VALIDATOR.validate(target, errors);
		assertOneErrorWithMessage("The Object 'arbitraryObject' must implement MethodRule or TestRule.");
	}

	private void assertOneErrorWithMessage(String message) {
		assertNumberOfErrors(1);
		assertEquals("Wrong error message:", message, errors.get(0).getMessage());
	}
	
	private void assertNumberOfErrors(int numberOfErrors) {
		assertEquals("Wrong number of errors:", numberOfErrors, errors.size());
	}
}
