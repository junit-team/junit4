/**
 * 
 */
package org.junit.internal.runners.links;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.internal.runners.model.TestElement;
import org.junit.internal.runners.model.FrameworkMethod;

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
		List<Throwable> fErrors = new ArrayList<Throwable>();
		fErrors.clear();
		try {
			fNext.evaluate();
		} catch (Throwable e) {
			fErrors.add(e);
		} finally {
			List<FrameworkMethod> afters= fElement.getAfters();
			for (FrameworkMethod each : afters)
				try {
					each.invokeExplosively(fTarget);
				} catch (Throwable e) {
					fErrors.add(e);
				}
		}
		if (fErrors.isEmpty())
			return;
		if (fErrors.size() == 1)
			throw fErrors.get(0);
		throw new MultipleFailureException(fErrors);
	}
}