/**
 * 
 */
package org.junit.internal.runners.statements;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class RunBefores extends Statement {
	private final Statement fNext;

	private final Object fTarget;

	private final List<FrameworkMethod> fBefores;

	public RunBefores(Statement next, List<FrameworkMethod> befores, Object target) {
		fNext= next;
		fBefores= befores;
		fTarget= target;
	}

	@Override
	public void evaluate() throws Throwable {
		for (FrameworkMethod before : fBefores)
			before.invokeExplosively(fTarget);
		fNext.evaluate();
	}
}