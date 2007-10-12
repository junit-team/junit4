/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.TestElement;

public class RunBefores extends Statement {
	private final Statement fNext;

	private final TestElement fElement;

	private final Object fTarget;

	public RunBefores(Statement next, TestElement element, Object target) {
		fNext= next;
		fElement= element;
		fTarget= target;
	}

	@Override
	public void evaluate() throws Throwable {
		fElement.runBefores(fTarget);
		fNext.evaluate();
	}
}