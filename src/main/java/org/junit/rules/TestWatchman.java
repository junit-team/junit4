package org.junit.rules;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
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
 * 
 * @deprecated {@link MethodRule} is deprecated.  
 *             Use {@link TestWatcher} implements {@link TestRule} instead.
 */
@Deprecated
public class TestWatchman implements MethodRule {
	public Statement apply(final Statement base, final FrameworkMethod method,
			Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				starting(method);
				try {
					base.evaluate();
					succeeded(method);
				} catch (AssumptionViolatedException e) {
					throw e;
				} catch (Throwable t) {
					failed(t, method);
					throw t;
				} finally {
					finished(method);
				}
			}
		};
	}

	/**
	 * Invoked when a test method succeeds
	 * 
	 * @param method
	 */
	public void succeeded(FrameworkMethod method) {
	}

	/**
	 * Invoked when a test method fails
	 * 
	 * @param e 
	 * @param method
	 */
	public void failed(Throwable e, FrameworkMethod method) {
	}

	/**
	 * Invoked when a test method is about to start
	 * 
	 * @param method  
	 */
	public void starting(FrameworkMethod method) {
	}


	/**
	 * Invoked when a test method finishes (whether passing or failing)
	 * 
	 * @param method  
	 */
	public void finished(FrameworkMethod method) {
	}
}
