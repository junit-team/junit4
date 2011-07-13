package org.junit.tests.experimental.rules.tests;

import org.junit.ClassRule;
import org.junit.rules.TestRule;

public class TestWithNullClassRule {
	@ClassRule
	public static TestRule RULE = null;
}