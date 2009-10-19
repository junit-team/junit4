/**
 * Created Oct 19, 2009
 */
package org.junit.rules;

import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * A ClassRule is the class-level analogue to a
 * {@link org.junit.rules.MethodRule}.
 * 
 * @author Alistair A. Israel
 */
public interface ClassRule {

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
