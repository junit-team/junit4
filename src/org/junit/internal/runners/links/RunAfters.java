/**
 * 
 */
package org.junit.internal.runners.links;

import java.util.List;

import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.internal.runners.model.TestElement;
import org.junit.internal.runners.model.TestMethod;
import org.junit.runner.notification.StoppedByUserException;

public class RunAfters extends Statement {
	private final Statement fNext;

	private final TestElement fElement;

	private final Object fTarget;

	public RunAfters(Statement next, TestElement element, Object target) {
		fNext= next;
		fElement= element;
		fTarget= target;
	}

	@Override
	public void evaluate() throws Throwable {
		MultipleFailureException errors= new MultipleFailureException();
		try {
			fNext.evaluate();
		} catch (StoppedByUserException e) {
			throw e; // TODO this is ugly. if we can eliminate pleaseStop(), we can remove it
		} catch (Throwable e) {
			errors.add(e);
		} finally {
			List<TestMethod> afters= fElement.getAfters();
			for (TestMethod each : afters)
				try {
					each.invokeExplosively(fTarget);
				} catch (Throwable e) {
					errors.add(e);
				}
		}
		errors.assertEmpty();
	}
}