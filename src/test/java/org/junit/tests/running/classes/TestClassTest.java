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
}
