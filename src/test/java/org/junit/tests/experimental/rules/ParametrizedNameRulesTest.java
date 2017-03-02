package org.junit.tests.experimental.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;

/**
 * @author Maxim Galushka
 */
@RunWith(Parameterized.class)
public class ParametrizedNameRulesTest {

    @Rule
    public TestName testName = new TestName();

    public ParametrizedNameRulesTest(String s) {
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{""},
                new Object[]{"\n"},
                new Object[]{"\r\n"},
                new Object[]{"\r"},
                new Object[]{"\u0085"},
                new Object[]{"\u2028"},
                new Object[]{"\u2029"}
        );
    }

    @Test
    public void test() throws Exception {
        assertNotNull(testName.getMethodName());
    }
}
