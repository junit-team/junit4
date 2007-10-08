/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.internal.runners.model.Roadie;

public class ExpectedException extends Link {
	private Link fNext;
	private final Class<? extends Throwable> fExpected;
	
	public ExpectedException(Link next, Class<? extends Throwable> expected) {
		fNext= next;
		fExpected= expected;
	}
	
	@Override
	public void run(Roadie context) {
		try {
			fNext.run(context);
			context.addFailure(new AssertionError("Expected exception: "
					+ fExpected.getName()));
		} catch (AssumptionViolatedException e) {
			// Do nothing
		} catch (Throwable e) {
			// TODO: (Oct 8, 2007 10:51:42 AM) Do I need isUnexpected?
			if (!fExpected.isAssignableFrom(e.getClass())) {
				String message= "Unexpected exception, expected<"
							+ fExpected.getName() + "> but was<"
							+ e.getClass().getName() + ">";
				context.addFailure(new Exception(message, e));
			}
		}
	}
}