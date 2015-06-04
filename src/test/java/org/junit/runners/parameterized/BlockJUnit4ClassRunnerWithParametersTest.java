package org.junit.runners.parameterized;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.TestClass;

public class BlockJUnit4ClassRunnerWithParametersTest {
    private static final List<Object> NO_PARAMETERS = emptyList();

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
}