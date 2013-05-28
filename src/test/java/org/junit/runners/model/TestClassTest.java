package org.junit.runners.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.rules.TemporaryFolder;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TestClassTest {

    public static class ClassWithAnnotatedFields {
        @DataPoint
        private String field1 = "field1";

        @DataPoint
        private String field2 = "field2";

        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        private String unannotated = "field3";
    }

    @Test
    public void annotatedFieldsAreReturned() {
        Set<FrameworkField> annotatedFields = new TestClass(ClassWithAnnotatedFields.class).getAnnotatedFields();
        assertThat(annotatedFields.size(), is(3));

        Set<String> fieldNames = new HashSet<String>();
        for (FrameworkField field : annotatedFields) {
            fieldNames.add(field.getName());
        }

        assertThat(fieldNames, hasItems("field1", "field2", "folder"));
    }

    public static class ClassWithAnnotatedMethods {
        @Before
        public void before() {
        }

        @Test
        public void test1() {
        }

        @Test
        public void test2() {
        }

        @After
        public void after() {
        }

        public void unannotated() {
        }
    }

    @Test
    public void annotatedMethodsAreReturned() {
        Set<FrameworkMethod> annotatedMethods = new TestClass(ClassWithAnnotatedMethods.class).getAnnotatedMethods();
        assertThat(annotatedMethods.size(), is(4));

        Set<String> methodNames = new HashSet<String>();
        for (FrameworkMethod method : annotatedMethods) {
            methodNames.add(method.getName());
        }

        assertThat(methodNames, hasItems("before", "after", "test1", "test2"));
    }
}
