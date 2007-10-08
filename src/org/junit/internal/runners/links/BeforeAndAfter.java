/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.TestElement;
import org.junit.internal.runners.model.Roadie;


public class BeforeAndAfter extends Link {
	private final Link fNext;
	private final TestElement fElement;
	
	public BeforeAndAfter(Link next, TestElement element) {
		fNext= next;
		fElement = element;
	}

	@Override
	public void run(final Roadie context) throws Throwable {
		try {
			if (fElement.runBefores(context))
				fNext.run(context);
		} finally {
			fElement.runAfters(context);
		}
	}
}