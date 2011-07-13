package org.junit.tests.experimental.rules.tests;

import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;

public class TestWithProtectedClassRule {
	@ClassRule
	protected static TestRule temporaryFolder = new TemporaryFolder();
}