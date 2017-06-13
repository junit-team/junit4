package org.junit.runners.parameterized;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.ExpectedException.none;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.TestClass;

public class BlockJUnit4ClassRunnerWithParametersTest {
    private static final List<Object> NO_PARAMETERS = emptyList();

    @Rule
    public final ExpectedException thrown = none();

    @RunWith(Parameterized.class)
    @DummyAnnotation
    public static class ClassWithParameterizedAnnotation {
        @Test
        public void dummyTest() {
        }
    }

    @Test
    public void hasAllAnnotationsExceptRunWith() throws Exception {
        TestWithParameters testWithParameters = new TestWithParameters(
                "dummy name", new TestClass(
                        ClassWithParameterizedAnnotation.class), NO_PARAMETERS);
        BlockJUnit4ClassRunnerWithParameters runner = new BlockJUnit4ClassRunnerWithParameters(
                testWithParameters);
        Annotation[] annotations = runner.getRunnerAnnotations();
        assertEquals(1, annotations.length);
        assertEquals(annotations[0].annotationType(), DummyAnnotation.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private static @interface DummyAnnotation {
    }

    @RunWith(Parameterized.class)
    public static class ClassWithPrivateParameter {
        @Parameterized.Parameter
        private String parameter;

        @Test
        public void dummyTest() {
        }
    }

    @Test
    public void providesHelpfulMessageIfParameterFieldCannotBeSet()
            throws Exception {
        TestWithParameters testWithParameters = new TestWithParameters(
                "dummy name",
                new TestClass(ClassWithPrivateParameter.class),
                Collections.<Object>singletonList("dummy parameter"));
        BlockJUnit4ClassRunnerWithParameters runner = new BlockJUnit4ClassRunnerWithParameters(
                testWithParameters);

        thrown.expect(IllegalAccessException.class);
        thrown.expectCause(instanceOf(IllegalAccessException.class));
        thrown.expectMessage("Cannot set parameter 'parameter'. Ensure that the field 'parameter' is public.");

        runner.createTest();
    }
}