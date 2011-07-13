package org.junit.tests.experimental.rules.tests;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;

public class TestWithNonStaticTestRule {
	@Rule
	public TestRule temporaryFolder = new TemporaryFolder();
}