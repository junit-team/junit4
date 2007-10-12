/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.Assume.AssumptionViolatedException;

public class IgnoreViolatedAssumptions extends Link {
	Link fNext;
	public IgnoreViolatedAssumptions(Link next) {
		fNext= next;
	}
	
	@Override
	public void run() throws Throwable {
		try {
			fNext.run();
		} catch (AssumptionViolatedException e) {
			// Do nothing
		}
	}
}