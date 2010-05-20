package org.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.rules.MethodRule;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * TODO: fix
 * 
 * Annotates fields that contain rules. Such a field must be public, not
 * static, and a subtype of {@link MethodRule}. For more information,
 * see {@link MethodRule}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassRule {
	// TODO: check javadoc
	
	/**
	 * A ClassRule is the class-level analogue to a
	 * {@link org.junit.rules.MethodRule}.
	 * 
	 * @author Alistair A. Israel
	 */
	public static interface Value {
		/**
		 * Modifies the class-running {@link Statement} to implement an additional,
		 * class-level test-running rule.
		 * 
		 * @param base
		 *            The {@link Statement} to be modified
		 * @param method
		 *            The {@link TestClass} to be run
		 * @return a new statement, which may be the same as {@code base}, a wrapper
		 *         around {@code base}, or a completely new Statement.
		 */
		Statement apply(Statement base, TestClass testClass);
	}
}