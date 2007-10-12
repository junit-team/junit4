/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.Assume.AssumptionViolatedException;

public class IgnoreViolatedAssumptions extends Statement {
	Statement fNext;
	public IgnoreViolatedAssumptions(Statement next) {
		fNext= next;
	}
	
	@Override
	public void evaluate() throws Throwable {
		try {
			fNext.evaluate();
		} catch (AssumptionViolatedException e) {
			// Do nothing
		}
	}
}