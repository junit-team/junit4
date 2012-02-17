package org.junit.rules;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * TestWatcher is a base class for Rules that take note of the testing
 * action, without modifying it. For example, this class will keep a log of each
 * passing and failing test:
 * 
 * <pre>
 * public static class WatchmanTest {
 * 	private static String watchedLog;
 * 
 * 	&#064;Rule
 * 	public MethodRule watchman= new TestWatcher() {
 * 		&#064;Override
 * 		protected void failed(Description d) {
 * 			watchedLog+= d + &quot;\n&quot;;
 * 		}
 * 
 * 		&#064;Override
 * 		protected void succeeded(Description d) {
 * 			watchedLog+= d + &quot; &quot; + &quot;success!\n&quot;;
 * 		}
 * 	};
 * 
 * 	&#064;Test
 * 	public void fails() {
 * 		fail();
 * 	}
 * 
 * 	&#064;Test
 * 	public void succeeds() {
 * 	}
 * }
 * </pre>
 */
public abstract class TestWatcher implements TestRule {
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				starting(description);
				try {
					base.evaluate();
					succeeded(description);
				} catch (AssumptionViolatedException e) {
					throw e;
				} catch (Throwable t) {
					failed(t, description);
					throw t;
				} finally {
					finished(description);
				}
			}
		};
	}

	/**
	 * Invoked when a test succeeds
	 * 
	 * @param description
	 */
	protected void succeeded(Description description) {
	}

	/**
	 * Invoked when a test fails
	 * 
	 * @param e 
	 * @param description
	 */
	protected void failed(Throwable e, Description description) {
	}

	/**
	 * Invoked when a test is about to start
	 * 
	 * @param description  
	 */
	protected void starting(Description description) {
	}


	/**
	 * Invoked when a test method finishes (whether passing or failing)
	 * 
	 * @param description  
	 */
	protected void finished(Description description) {
	}
}
