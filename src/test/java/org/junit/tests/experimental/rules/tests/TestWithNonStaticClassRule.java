package org.junit.tests.experimental.rules.tests;

import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;

public class TestWithNonStaticClassRule {
	@ClassRule
	public TestRule temporaryFolder = new TemporaryFolder();
}