package org.junit.tests.running.classes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.TestClass;

public class TestClassTest {
    public static class TwoConstructors {
        public TwoConstructors() {
        }

        public TwoConstructors(int x) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void complainIfMultipleConstructors() {
        TestClass.forClass(TwoConstructors.class);
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
        TestClass testClass = TestClass.forClass(SubclassWithField.class);
        assertThat(testClass.getAnnotatedFields(Rule.class).size(), is(1));
    }

    public static class OuterClass {
        public class NonStaticInnerClass {
        }
    }

    @Test
    public void identifyNonStaticInnerClass() {
        TestClass testClass = TestClass.forClass(OuterClass.NonStaticInnerClass.class);
        assertThat(testClass.isANonStaticInnerClass(), is(true));
    }

    public static class OuterClass2 {
        public static class StaticInnerClass {
        }
    }

    @Test
    public void dontMarkStaticInnerClassAsNonStatic() {
        TestClass testClass = TestClass.forClass(OuterClass2.StaticInnerClass.class);
        assertThat(testClass.isANonStaticInnerClass(), is(false));
    }

    public static class SimpleClass {
    }

    @Test
    public void dontMarkNonInnerClassAsInnerClass() {
        TestClass testClass = TestClass.forClass(SimpleClass.class);
        assertThat(testClass.isANonStaticInnerClass(), is(false));
    }
        
    public static class FieldAnnotated {
    	@Rule
    	public String fieldThatShouldBeMatched = "andromeda";
    	
    	@Rule
    	public boolean fieldThatShouldNotBeMachted;
    }
    
    @Test
    public void annotatedFieldValues() {
    	TestClass testClass = TestClass.forClass(FieldAnnotated.class);
    	List<String> values = testClass.getAnnotatedFieldValues(new FieldAnnotated(), Rule.class, String.class);
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
    	TestClass testClass = TestClass.forClass(MethodsAnnotated.class);
    	List<String> values = testClass.getAnnotatedMethodValues(new MethodsAnnotated(), Ignore.class, String.class);
    	assertThat(values, hasItem("jupiter"));
    	assertThat(values.size(), is(1));
    }
}
