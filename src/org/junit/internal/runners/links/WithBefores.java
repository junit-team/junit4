/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.TestElement;

public class WithBefores extends Link {
	private final Link fNext;

	private final TestElement fElement;

	private final Object fTarget;

	public WithBefores(Link next, TestElement element, Object target) {
		fNext= next;
		fElement= element;
		fTarget= target;
	}

	@Override
	public void run() throws Throwable {
		fElement.runBefores(fTarget);
		fNext.run();
	}
}