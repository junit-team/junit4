package org.junit.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * A base class for Rules (like TemporaryFolder) that set up an external
 * resource before a test (a file, socket, server, database connection, etc.),
 * and guarantee to tear it down afterward:
 *
 * <pre>
 * public static class UsesExternalResource {
 *  Server myServer= new Server();
 *
 *  &#064;Rule
 *  public ExternalResource resource= new ExternalResource() {
 *      &#064;Override
 *      protected void before() throws Throwable {
 *          myServer.connect();
 *         };
 *
 *      &#064;Override
 *      protected void after() {
 *          myServer.disconnect();
 *         };
 *     };
 *
 *  &#064;Test
 *  public void testFoo() {
 *      new Client().run(myServer);
 *     }
 * }
 * </pre>
 *
 * @since 4.7
 */
public abstract class ExternalResource implements TestRule {
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();

                List<Throwable> errors = new ArrayList<Throwable>();
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    try {
                        after();
                    } catch (Throwable t) {
                        errors.add(t);
                    }
                }
                MultipleFailureException.assertEmpty(errors);
            }
        };
    }

    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable if setup fails (which will disable {@code after}
     */
    protected void before() throws Throwable {
        // do nothing
    }

    /**
     * Override to tear down your specific external resource.
     */
    protected void after() {
        // do nothing
    }
}
