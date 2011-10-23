package org.junit.runners.model;

import java.util.List;

import org.junit.Ignore;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.CustomizableJUnit4ClassRunner;

public interface FrameworkTest {

	Description createDescription();

	/**
	 * Return <code>true</code> if test is marked to be ignored by
	 * {@link CustomizableJUnit4ClassRunner}. {@link FrameworkMember} uses
	 * {@link Ignore} annotation to determine this.
	 * 
	 * @return
	 */
	boolean isIgnored();

	Statement createStatement(Object testInstance, List<TestRule> testRules);
}
