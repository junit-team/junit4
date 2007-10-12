/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.internal.runners.model.TestElement;

public class WithAfters extends Link {
	private final Link fNext;

	private final TestElement fElement;

	private final Object fTarget;

	public WithAfters(Link next, TestElement element, Object target) {
		fNext= next;
		fElement= element;
		fTarget= target;
	}

	@Override
	public void run() throws Throwable {
		MultipleFailureException errors= new MultipleFailureException();
		try {
			fNext.run();
		} catch (Throwable e) {
			errors.add(e);
		}
		
		try {
			// TODO: (Oct 12, 2007 9:51:00 AM) inline runAfters
			fElement.runAfters(fTarget);
		} catch (Throwable e) {
			errors.add(e);
		}
		
		errors.throwUnlessEmpty();
	}
}