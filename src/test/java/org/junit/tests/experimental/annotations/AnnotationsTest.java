package org.junit.tests.experimental.annotations;

import org.junit.Test;
import org.junit.experimental.annotations.Annotations;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.annotations.Annotations.Exclude;
import static org.junit.experimental.annotations.Annotations.Include;

public class AnnotationsTest {

    private void run(Class<?> testClass, boolean expectSucces, int expectedRunCount) {
        final Result result = new JUnitCore().run(testClass);
        assertTrue("Wrong expected test results", result.wasSuccessful() == expectSucces);
        assertEquals("Wrong expected run count", expectedRunCount, result.getRunCount());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public static @interface A {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public static @interface B {}

    @RunWith(Annotations.class)
    public static class NoClauses {

        @Test
        public void test() {}

    }

    @Test
    public void testNoClauses() {
        run(NoClauses.class, true, 1);
    }

    @RunWith(Annotations.class)
    @Include(A.class)
    public static class IncludeClause {

        @Test
        public void excludedNoAnnotation() {}

        @A
        @Test
        public void included() {}

        @B
        @Test
        public void excludedWrongAnnotation() {}

    }

    @Test
    public void testIncludeClause() {
        run(IncludeClause.class, true, 1);
    }

    @RunWith(Annotations.class)
    @Exclude(B.class)
    public static class ExcludeClause {

        @Test
        public void includedNoAnnotation() {}

        @A
        @Test
        public void includedWrongAnnotation() {}

        @B
        @Test
        public void excluded() {}

    }

    @Test
    public void testExcludeClause() {
        run(ExcludeClause.class, true, 2);
    }

    @RunWith(Annotations.class)
    @Include(A.class)
    @Exclude(B.class)
    public static class IncludeExcludeClause {

        @Test
        public void excludedNoAnnotation() {}

        @A
        @Test
        public void includedWrongAnnotation() {}

        @B
        @Test
        public void excluded() {}

    }

    @Test
    public void testIncludeExcludeClause() {
        run(IncludeExcludeClause.class, true, 1);
    }

}
