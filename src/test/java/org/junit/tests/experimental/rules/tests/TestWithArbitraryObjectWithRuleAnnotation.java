package org.junit.tests.experimental.rules.tests;

import org.junit.Rule;

public class TestWithArbitraryObjectWithRuleAnnotation {
	@Rule
	public Object arbitraryObject = 1;
}