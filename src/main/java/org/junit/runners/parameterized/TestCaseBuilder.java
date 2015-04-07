package org.junit.runners.parameterized;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.runners.Parameterized;

/**
 * Class for creating test data for {@link Parameterized} tests.
 * 
 * Create a new instance of TestCaseBuilder and add some parameters with
 * {@link #testCase(Object...)}. Then the method {@link #testData()} will return
 * the test data in format needed by {@link Parameterized}.
 * 
 * <pre>
 * &#064;Parameters
 * public static Collection&lt;Object[]&gt; data() {
 *     TestCaseBuilder builder = new TestCaseBuilder();
 *     builder.testCase(0, 0);
 *     builder.testCase(1, 1);
 *     builder.testCase(2, 1);
 *     builder.testCase(3, 2);
 *     builder.testCase(4, 3);
 *     builder.testCase(5, 5);
 *     builder.testCase(6, 8);
 * 
 *     return builder.testData();
 * }
 * </pre>
 * 
 * @author martin@krueger-mails.de
 *
 */
public class TestCaseBuilder {

    private Collection<Object[]> testData = new ArrayList<Object[]>();

    public void testCase(Object... parameters) {
        testData.add(parameters);
    }

    /**
     * @return the test data
     */
    public Collection<Object[]> testData() {
        return testData;
    }

}
