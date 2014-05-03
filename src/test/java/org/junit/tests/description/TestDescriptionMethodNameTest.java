package org.junit.tests.description;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 03.05.14
 */
@RunWith(Parameterized.class)
public class TestDescriptionMethodNameTest {

    private String methodName;

    public TestDescriptionMethodNameTest(String methodName) {
        this.methodName = methodName;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getMethodNames() {
        return Arrays.asList(
                new Object[]{"simple"},
                new Object[]{"with space"},
                new Object[]{"[]!@#$%^&*()"},
                new Object[]{""},
                new Object[]{"\t"},
                new Object[]{"\n"},
                new Object[]{"\r\n"},
                new Object[]{"\r"},
                new Object[]{"\u0085"},
                new Object[]{"\u2028"},
                new Object[]{"\u2029"}
        );
    }

    @Test
    public void methodNameTest() throws Exception {
        Description description = Description.createTestDescription("some-class-name", methodName);
        assertNotNull("Method name should be not null", description.getMethodName());
        assertEquals(methodName, description.getMethodName());
    }
}
