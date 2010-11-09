package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * TODO: fix javadoc
 * 
 * TestWatchman is a base class for Rules that take note of the testing
 * action, without modifying it. For example, this class will keep a log of each
 * passing and failing test:
 * 
 * <pre>
 * public static class WatchmanTest {
 * 	private static String watchedLog;
 * 
 * 	&#064;Rule
 * 	public MethodRule watchman= new TestWatchman() {
 * 		&#064;Override
 * 		public void failed(Throwable e, FrameworkMethod method) {
 * 			watchedLog+= method.getName() + &quot; &quot; + e.getClass().getSimpleName()
 * 					+ &quot;\n&quot;;
 * 		}
 * 
 * 		&#064;Override
 * 		public void succeeded(FrameworkMethod method) {
 * 			watchedLog+= method.getName() + &quot; &quot; + &quot;success!\n&quot;;
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
public class TestRuleTestWatchman implements TestRule {
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				starting(description);
				try {
					base.evaluate();
					succeeded(description);
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
	public void succeeded(Description description) {
	}

	/**
	 * Invoked when a test fails
	 * 
	 * @param e 
	 * @param description
	 */
	public void failed(Throwable e, Description description) {
	}

	/**
	 * Invoked when a test is about to start
	 * 
	 * @param description  
	 */
	public void starting(Description description) {
	}


	/**
	 * Invoked when a test method finishes (whether passing or failing)
	 * 
	 * @param description  
	 */
	public void finished(Description description) {
	}
}
