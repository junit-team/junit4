/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.EachTestNotifier;
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
	public void run(final EachTestNotifier context) throws Throwable {
		try {
			if (fElement.runBefores(context, fTarget))
				fNext.run(context);
		} finally {
			fElement.runAfters(context, fTarget);
		}
	}
}