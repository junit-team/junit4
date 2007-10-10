/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.FailureListener;

public class IgnoringViolatedAssumptions extends Link {
	Link fNext;
	public IgnoringViolatedAssumptions(Link next) {
		fNext= next;
	}
	
	
	
	@Override
	public void run(final FailureListener listener) {
		fNext.run(new FailureListener() {
			@Override
			public void handleFailure(Throwable error) {
				if (!(error instanceof AssumptionViolatedException))
					listener.addFailure(error);
			}		
		});
	}
}