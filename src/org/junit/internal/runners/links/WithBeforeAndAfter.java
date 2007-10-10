/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.experimental.theories.FailureListener;
import org.junit.internal.runners.model.TestElement;


public class WithBeforeAndAfter extends Link {
	private final Link fNext;
	private final TestElement fElement;
	private final Object fTarget;
	
	public WithBeforeAndAfter(Link next, TestElement element, Object target) {
		fNext= next;
		fElement= element;
		fTarget= target;
	}

	@Override
	public void run(final FailureListener listener) {
		fElement.runProtected(listener, new Runnable() {
			public void run() {
				fNext.run(listener);
			}		
		}, fTarget);
	}
}