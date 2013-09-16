package org.junit.tests.running.classes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

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
    	public String fieldThatShouldBeMatched = "andromeda";
    	
    	@Rule
    	public boolean fieldThatShouldNotBeMachted;
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
    	public String methodToBeMatched() { 
    		return "jupiter";
    	}
    	
    	@Ignore
    	@Test
    	public int methodOfWrongType() {
    		return 0;
    	}
    }
    
    @Test
    public void annotatedMethodValues() {
    	TestClass tc = new TestClass(MethodsAnnotated.class);
    	List<String> values = tc.getAnnotatedMethodValues(new MethodsAnnotated(), Ignore.class, String.class);
    	assertThat(values, hasItem("jupiter"));
    	assertThat(values.size(), is(1));
    }

    @Test
    public void annotationToMethods() {
        TestClass tc = new TestClass(MethodsAnnotated.class);
        Map<Class<? extends Annotation>, List<FrameworkMethod>> annotationToMethods = tc.getAnnotationToMethods();
        List<FrameworkMethod> methods = annotationToMethods.get(Ignore.class);
        assertThat(methods.size(), is(2));
    }

    @Test
    public void annotationToMethodsReturnsUnmodifiableMap() {
        TestClass tc = new TestClass(MethodsAnnotated.class);
        Map<Class<? extends Annotation>, List<FrameworkMethod>> annotationToMethods = tc.getAnnotationToMethods();
        exception.expect(UnsupportedOperationException.class);
        annotationToMethods.put(Ignore.class, null);
    }

    @Test
    public void annotationToMethodsReturnsValuesInTheMapThatAreUnmodifiable() {
        TestClass tc = new TestClass(MethodsAnnotated.class);
        Map<Class<? extends Annotation>, List<FrameworkMethod>> annotationToMethods = tc.getAnnotationToMethods();
        List<FrameworkMethod> methods = annotationToMethods.get(Ignore.class);
        exception.expect(UnsupportedOperationException.class);
        methods.add(null);
    }
    
    @Test
    public void annotationToFields() {
        TestClass tc = new TestClass(FieldAnnotated.class);
        Map<Class<? extends Annotation>, List<FrameworkField>> annotationToFields = tc.getAnnotationToFields();
        List<FrameworkField> fields = annotationToFields.get(Rule.class);
        assertThat(fields.size(), is(2));
    }

    @Test
    public void annotationToFieldsReturnsUnmodifiableMap() {
        TestClass tc = new TestClass(FieldAnnotated.class);
        Map<Class<? extends Annotation>, List<FrameworkField>> annotationToFields = tc.getAnnotationToFields();
        exception.expect(UnsupportedOperationException.class);
        annotationToFields.put(Rule.class, null);
    }

    @Test
    public void annotationToFieldsReturnsValuesInTheMapThatAreUnmodifiable() {
        TestClass tc = new TestClass(FieldAnnotated.class);
        Map<Class<? extends Annotation>, List<FrameworkField>> annotationToFields = tc.getAnnotationToFields();
        List<FrameworkField> fields = annotationToFields.get(Rule.class);
        exception.expect(UnsupportedOperationException.class);
        fields.add(null);
    }

    public static class MultipleFieldsAnnotated {
        @DataPoint
        public String a = "testing a";

        @Rule
        public boolean b;

        @DataPoint
        public String c = "testing c";

        @Rule
        public boolean d;
    }

    @Test
    public void annotationToFieldsReturnsKeysInADeterministicOrder() {
        TestClass tc = new TestClass(MultipleFieldsAnnotated.class);
        Map<Class<? extends Annotation>, List<FrameworkField>> annotationToFields = tc.getAnnotationToFields();
        List<Class<? extends Annotation>> keys = new ArrayList<Class<? extends Annotation>>();
        for (Class<? extends Annotation> annotation : annotationToFields.keySet()) {
            keys.add(annotation);
        }
        assertThat(keys.get(0), CoreMatchers.<Class<? extends Annotation>>is(DataPoint.class));
        assertThat(keys.get(1), CoreMatchers.<Class<? extends Annotation>>is(Rule.class));
    }
}
