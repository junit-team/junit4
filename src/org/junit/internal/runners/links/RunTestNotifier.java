/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;

public class RunTestNotifier extends Notifier {
	private final Statement fNext;

	public RunTestNotifier(Statement next) {
		fNext= next;
	}

	@Override
	public void run(EachTestNotifier context) {
		context.fireTestStarted();
		try {
			fNext.evaluate();
		} catch (AssumptionViolatedException e) {
			context.addIgnorance(e);
		} catch (Throwable e) {
			context.addFailure(e);
		} finally {
			context.fireTestFinished();
		}
	}
}