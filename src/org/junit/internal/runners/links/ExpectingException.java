/**
 * 
 */
package org.junit.internal.runners.links;


public class ExpectingException extends Link {
	private Link fNext;
	private final Class<? extends Throwable> fExpected;
	
	public ExpectingException(Link next, Class<? extends Throwable> expected) {
		fNext= next;
		fExpected= expected;
	}
	
	@Override
	public void run() throws Exception {
		boolean complete = false;
		try {
			fNext.run();
			complete = true;
		} catch (Throwable e) {
			if (!fExpected.isAssignableFrom(e.getClass())) {
				String message= "Unexpected exception, expected<"
							+ fExpected.getName() + "> but was<"
							+ e.getClass().getName() + ">";
				throw new Exception(message, e);
			}
		}
		if (complete)
			throw new AssertionError("Expected exception: "
					+ fExpected.getName());
	}
}