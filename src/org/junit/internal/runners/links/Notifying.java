/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.Roadie;

public class Notifying extends Link {
	private final Link fNext;

	public Notifying(Link next) {
		fNext= next;
	}

	@Override
	public void run(Roadie context) {
		context.fireTestStarted();
		try {
			fNext.run(context);
		} catch (Throwable e) {
			context.addFailure(e);
		} finally {
			context.fireTestFinished();
		}
	}
}