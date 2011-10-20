package org.junit.runners.model;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runners.CustomizableJUnit4ClassRunner;

public interface FrameworkTest {

	Description createDescription(TestClass testClass);

	/**
	 * Return <code>true</code> if test is marked to be ignored by {@link CustomizableJUnit4ClassRunner}.
	 * {@link FrameworkMember} uses {@link Ignore} annotation to determine this.
	 * @return
	 */
	boolean shouldBeIgnored();

	Statement createStatement(Object test);
}
