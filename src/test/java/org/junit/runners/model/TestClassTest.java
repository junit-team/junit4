package org.junit.runners.model;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

public class TestClassTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public static class TwoConstructors {
        public TwoConstructors() {
        }

        public TwoConstructors(int x) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void complainIfMultipleConstructors() {
        new TestClass(TwoConstructors.class);
    }

    public static class SuperclassWithField {
        @Rule
        public TestRule x;
    }

    public static class SubclassWithField extends SuperclassWithField {
        @Rule
        public TestRule x;
    }

    @Test
    public void fieldsOnSubclassesShadowSuperclasses() {
        assertThat(new TestClass(SubclassWithField.class).getAnnotatedFields(
                Rule.class).size(), is(1));
    }

    public static class OuterClass {
        public class NonStaticInnerClass {
        }
    }

    @Test
    public void identifyNonStaticInnerClass() {
        assertThat(
                new TestClass(OuterClass.NonStaticInnerClass.class)
                        .isANonStaticInnerClass(),
                is(true));
    }

    public static class OuterClass2 {
        public static class StaticInnerClass {
        }
    }

    @Test
    public void dontMarkStaticInnerClassAsNonStatic() {
        assertThat(
                new TestClass(OuterClass2.StaticInnerClass.class)
                        .isANonStaticInnerClass(),
                is(false));
    }

    public static class SimpleClass {
    }

    @Test
    public void dontMarkNonInnerClassAsInnerClass() {
        assertThat(new TestClass(SimpleClass.class).isANonStaticInnerClass(),
                is(false));
    }

    public static class FieldAnnotated {
        @Rule
        public String fieldC= "andromeda";

        @Rule
        public boolean fieldA;

        @Rule
        public boolean fieldB;
    }

    @Test
    public void providesAnnotatedFieldsSortedByName() {
        TestClass tc= new TestClass(FieldAnnotated.class);
        List<FrameworkField> annotatedFields= tc.getAnnotatedFields();
        assertThat("Wrong number of annotated fields.", annotatedFields.size(), is(3));
        assertThat("First annotated field is wrong.", annotatedFields
            .iterator().next().getName(), is("fieldA"));
    }

    @Test
    public void annotatedFieldValues() {
        TestClass tc = new TestClass(FieldAnnotated.class);
        List<String> values = tc.getAnnotatedFieldValues(new FieldAnnotated(), Rule.class, String.class);
        assertThat(values, hasItem("andromeda"));
        assertThat(values.size(), is(1));
    }

    public static class MethodsAnnotated {
        @Ignore
        @Test
        public int methodC() {
            return 0;
        }

        @Ignore
        @Test
        public String methodA() {
            return "jupiter";
        }

        @Ignore
        @Test
        public int methodB() {
            return 0;
    	}
    }

    @Test
    public void providesAnnotatedMethodsSortedByName() {
    	TestClass tc = new TestClass(MethodsAnnotated.class);
    	List<FrameworkMethod> annotatedMethods = tc.getAnnotatedMethods();
    	assertThat("Wrong number of annotated methods.",
    	    annotatedMethods.size(), is(3));
    	assertThat("First annotated method is wrong.", annotatedMethods
    	    .iterator().next().getName(), is("methodA"));
    }

    @Test
    public void annotatedMethodValues() {
    	TestClass tc = new TestClass(MethodsAnnotated.class);
    	List<String> values = tc.getAnnotatedMethodValues(
    	    new MethodsAnnotated(), Ignore.class, String.class);
    	assertThat(values, hasItem("jupiter"));
    	assertThat(values.size(), is(1));
    }

    @Test
    public void isEqualToTestClassThatWrapsSameJavaClass() {
        TestClass testClass = new TestClass(DummyClass.class);
        TestClass testClassThatWrapsSameJavaClass = new TestClass(
                DummyClass.class);
        assertTrue(testClass.equals(testClassThatWrapsSameJavaClass));
    }

    @Test
    public void isEqualToTestClassThatWrapsNoJavaClassToo() {
        TestClass testClass = new TestClass(null);
        TestClass testClassThatWrapsNoJavaClassToo = new TestClass(null);
        assertTrue(testClass.equals(testClassThatWrapsNoJavaClassToo));
    }

    @Test
    public void isNotEqualToTestClassThatWrapsADifferentJavaClass() {
        TestClass testClass = new TestClass(DummyClass.class);
        TestClass testClassThatWrapsADifferentJavaClass = new TestClass(
                AnotherDummyClass.class);
        assertFalse(testClass.equals(testClassThatWrapsADifferentJavaClass));
    }

    @Test
    public void isNotEqualToNull() {
        TestClass testClass = new TestClass(DummyClass.class);
        assertFalse(testClass.equals(null));
    }

    private static class DummyClass {
    }

    private static class AnotherDummyClass {
    }

    @Test
    public void hasSameHashCodeAsTestClassThatWrapsSameJavaClass() {
        TestClass testClass = new TestClass(DummyClass.class);
        TestClass testClassThatWrapsSameJavaClass = new TestClass(
                DummyClass.class);
        assertEquals(testClass.hashCode(),
                testClassThatWrapsSameJavaClass.hashCode());
    }

    @Test
    public void hasHashCodeWithoutJavaClass() {
        TestClass testClass = new TestClass(null);
        testClass.hashCode();
        // everything is fine if no exception is thrown.
    }

    public static class PublicClass {

    }

    @Test
    public void identifiesPublicModifier() {
        TestClass tc = new TestClass(PublicClass.class);
        assertEquals("Wrong flag 'public',", true, tc.isPublic());
    }

    static class NonPublicClass {

    }
    
    @Test
    public void identifiesNonPublicModifier() {
        TestClass tc = new TestClass(NonPublicClass.class);
        assertEquals("Wrong flag 'public',", false, tc.isPublic());
    }

    @Ignore
    static class AnnotatedClass {
    }

    @Test
    public void presentAnnotationIsAvailable() {
        TestClass tc = new TestClass(AnnotatedClass.class);
        Annotation annotation = tc.getAnnotation(Ignore.class);
        assertTrue(Ignore.class.isAssignableFrom(annotation.getClass()));
    }

    @Test
    public void missingAnnotationIsNotAvailable() {
        TestClass tc = new TestClass(AnnotatedClass.class);
        Annotation annotation = tc.getAnnotation(RunWith.class);
        assertThat(annotation, is(nullValue()));
    }
}
