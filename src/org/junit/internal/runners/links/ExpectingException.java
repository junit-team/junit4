/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.EachTestNotifier;

public class ExpectingException extends Link {
	private Link fNext;
	private final Class<? extends Throwable> fExpected;
	
	public ExpectingException(Link next, Class<? extends Throwable> expected) {
		fNext= next;
		fExpected= expected;
	}
	
	@Override
	public void run(EachTestNotifier context) {
		try {
			fNext.run(context);
			context.addFailure(new AssertionError("Expected exception: "
					+ fExpected.getName()));
		} catch (Throwable e) {
			if (!fExpected.isAssignableFrom(e.getClass())) {
				String message= "Unexpected exception, expected<"
							+ fExpected.getName() + "> but was<"
							+ e.getClass().getName() + ">";
				context.addFailure(new Exception(message, e));
			}
		}
	}
}