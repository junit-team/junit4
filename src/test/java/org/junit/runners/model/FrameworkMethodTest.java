package org.junit.runners.model;

import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;

import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FrameworkMethodTest {
    @Rule
    public final ExpectedException thrown = none();

    @Test
    public void cannotBeCreatedWithoutUnderlyingField() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("FrameworkMethod cannot be created without an underlying method.");
        new FrameworkMethod(null);
    }

    @Test
    public void hasToStringWhichPrintsMethodName() throws Exception {
        Method method = ClassWithDummyMethod.class.getMethod("dummyMethod");
        FrameworkMethod frameworkMethod = new FrameworkMethod(method);
        assertTrue(frameworkMethod.toString().contains("dummyMethod"));
    }

    private static class ClassWithDummyMethod {
        @SuppressWarnings("unused")
        public void dummyMethod() {
        }
    }
}
