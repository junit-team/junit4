/**
 *
 */
package org.junit.internal.runners.notifiers;

import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runners.Notifier;
import org.junit.runners.Statement;

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
			context.fireTestIgnored();
		} catch (Throwable e) {
			context.addFailure(e);
		} finally {
			context.fireTestFinished();
		}
	}
}