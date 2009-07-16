package org.junit.rules;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * A base class for Rules (like TemporaryFolder) that set up an external
 * resource before a test (a file, socket, server, database connection, etc.),
 * and guarantee to tear it down afterward:
 * 
 * <pre>
 * public static class UsesExternalResource {
 * 	Server myServer= new Server();
 * 
 * 	&#064;Rule
 * 	public ExternalResource resource= new ExternalResource() {
 * 		&#064;Override
 * 		protected void before() throws Throwable {
 * 			myServer.connect();
 * 		};
 * 
 * 		&#064;Override
 * 		protected void after() {
 * 			myServer.disconnect();
 * 		};
 * 	};
 * 
 * 	&#064;Test
 * 	public void testFoo() {
 * 		new Client().run(myServer);
 * 	}
 * }
 * </pre>
 */
public abstract class ExternalResource implements MethodRule {
	public final Statement apply(final Statement base,
			FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before();
				try {
					base.evaluate();
				} finally {
					after();
				}
			}
		};
	}

	/**
	 * Override to set up your specific external resource.
	 * @throws if setup fails (which will disable {@code after}
	 */
	protected void before() throws Throwable {
		// do nothing
	}

	/**
	 * Override to tear down your specific external resource.
	 * @throws if setup fails (which will disable {@code after}
	 */
	protected void after() {
		// do nothing
	}
}
