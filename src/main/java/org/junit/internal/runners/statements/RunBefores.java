/**
 * 
 */
package org.junit.internal.runners.statements;

import java.util.List;

import org.junit.internal.runners.model.TestElement;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

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
		List<FrameworkMethod> befores= fElement.getBefores();
		for (FrameworkMethod before : befores)
			before.invokeExplosively(fTarget);
		fNext.evaluate();
	}
}