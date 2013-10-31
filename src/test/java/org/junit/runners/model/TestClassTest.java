package org.junit.runners.model;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

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
}
