package org.junit.rules;

import java.util.List;

import org.junit.internal.management.ManagementFactory;
import org.junit.internal.management.RuntimeMXBean;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The {@code DisableOnDebug} Rule allows you to label certain rules to be
 * disabled when debugging.
 * <p>
 * The most illustrative use case is for tests that make use of the
 * {@link Timeout} rule, when ran in debug mode the test may terminate on
 * timeout abruptly during debugging. Developers may disable the timeout, or
 * increase the timeout by making a code change on tests that need debugging and
 * remember revert the change afterwards or rules such as {@link Timeout} that
 * may be disabled during debugging may be wrapped in a {@code DisableOnDebug}.
 * <p>
 * The important benefit of this feature is that you can disable such rules
 * without any making any modifications to your test class to remove them during
 * debugging.
 * <p>
 * This does nothing to tackle timeouts or time sensitive code under test when
 * debugging and may make this less useful in such circumstances.
 * <p>
 * Example usage:
 * 
 * <pre>
 * public static class DisableTimeoutOnDebugSampleTest {
 * 
 *     &#064;Rule
 *     public TestRule timeout = new DisableOnDebug(new Timeout(20));
 * 
 *     &#064;Test
 *     public void myTest() {
 *         int i = 0;
 *         assertEquals(0, i); // suppose you had a break point here to inspect i
 *     }
 * }
 * </pre>
 * 
 * @since 4.12
 */
public class DisableOnDebug implements TestRule {
    private final TestRule rule;
    private final boolean debugging;

    /**
     * Create a {@code DisableOnDebug} instance with the timeout specified in
     * milliseconds.
     * 
     * @param rule to disable during debugging
     */
    public DisableOnDebug(TestRule rule) {
        this(rule, ManagementFactory.getRuntimeMXBean()
                .getInputArguments());
    }

    /**
     * Visible for testing purposes only.
     * 
     * @param rule the rule to disable during debugging
     * @param inputArguments
     *            arguments provided to the Java runtime
     */
    DisableOnDebug(TestRule rule, List<String> inputArguments) {
        this.rule = rule;
        debugging = isDebugging(inputArguments);
    }

    /**
     * @see TestRule#apply(Statement, Description)
     */
    public Statement apply(Statement base, Description description) {
        if (debugging) {
            return base;
        } else {
            return rule.apply(base, description);
        }
    }

    /**
     * Parses arguments passed to the runtime environment for debug flags
     * <p>
     * Options specified in:
     * <ul>
     * <li>
     * <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/jpda/conninv.html#Invocation"
     * >javase-6</a></li>
     * <li><a href="http://docs.oracle.com/javase/7/docs/technotes/guides/jpda/conninv.html#Invocation"
     * >javase-7</a></li>
     * <li><a href="http://docs.oracle.com/javase/8/docs/technotes/guides/jpda/conninv.html#Invocation"
     * >javase-8</a></li>
     * 
     * 
     * @param arguments
     *            the arguments passed to the runtime environment, usually this
     *            will be {@link RuntimeMXBean#getInputArguments()}
     * @return true if the current JVM was started in debug mode, false
     *         otherwise.
     */
    private static boolean isDebugging(List<String> arguments) {
        for (final String argument : arguments) {
            if ("-Xdebug".equals(argument) || argument.startsWith("-agentlib:jdwp")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the JVM is in debug mode. This method may be used
     * by test classes to take additional action to disable code paths that
     * interfere with debugging if required.
     * 
     * @return {@code true} if the current JVM is in debug mode, {@code false}
     *         otherwise
     */
    public boolean isDebugging() {
        return debugging;
    }

}
