package org.junit.tests.experimental.max;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;

public class DescriptionTest {

    @Test
    public void parseClass_whenCantParse() {
        assertNull(Description.TEST_MECHANISM.getTestClass());
    }

    @Test
    public void parseMethod_whenCantParse() {
        assertNull(Description.TEST_MECHANISM.getMethodName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSuiteDescription_whenZeroLength() {
        Description.createSuiteDescription("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSuiteDescription_whenNull() {
        Description.createSuiteDescription((String) null);
    }

    @Test
    public void parseClassAndMethodNoAnnotations() throws Exception {
        Description description = Description.createTestDescription(Description.class, "aTestMethod");

        assertThat(description.getClassName(), equalTo("org.junit.runner.Description"));
        assertThat(description.getMethodName(), equalTo("aTestMethod"));
        assertThat(description.getAnnotations().size(), equalTo(0));
    }

    @Test
    public void parseClassAndMethodWithAnnotations() throws Exception {
        Annotation[] annotations =
                DescriptionTest.class.getMethod("parseClassAndMethodWithAnnotations").getDeclaredAnnotations();

        Description description = Description.createTestDescription(Description.class, "aTestMethod", annotations);

        assertThat(description.getClassName(), equalTo("org.junit.runner.Description"));
        assertThat(description.getMethodName(), equalTo("aTestMethod"));
        assertThat(description.getAnnotations().size(), equalTo(1));
    }

    @Test
    public void parseClassNameAndMethodUniqueId() throws Exception {
        Description description = Description.createTestDescription("not a class name", "aTestMethod", 123);

        assertThat(description.getClassName(), equalTo("not a class name"));
        assertThat(description.getMethodName(), equalTo("aTestMethod"));
        assertThat(description.getAnnotations().size(), equalTo(0));
    }

    @Test
    public void sameNamesButDifferentUniqueIdAreNotEqual() throws Exception {
        assertThat(Description.createTestDescription("not a class name", "aTestMethod", 1),
                not(equalTo(Description.createTestDescription("not a class name", "aTestMethod", 2))));
    }

    @Test
    public void usesPassedInClassObject() throws Exception {
        class URLClassLoader2 extends URLClassLoader {
            URLClassLoader2(URL... urls) {
                super(urls);
            }

            @Override // just making public
            public Class<?> findClass(String name) throws ClassNotFoundException {
                return super.findClass(name);
            }
        }
        URL classpath = Sweet.class.getProtectionDomain().getCodeSource().getLocation();
        URLClassLoader2 loader = new URLClassLoader2(classpath);
        Class<?> clazz = loader.findClass(Sweet.class.getName());
        assertEquals(loader, clazz.getClassLoader());

        Description d = Description.createSuiteDescription(clazz);
        assertEquals(clazz, d.getTestClass());
        assertNull(d.getMethodName());
        assertEquals(1, d.getAnnotations().size());
        assertEquals(Ignore.class, d.getAnnotations().iterator().next().annotationType());

        d = Description.createTestDescription(clazz, "tessed");
        assertEquals(clazz, d.getTestClass());
        assertEquals("tessed", d.getMethodName());
        assertEquals(0, d.getAnnotations().size());

        d = Description.createTestDescription(clazz, "tessed", clazz.getMethod("tessed").getAnnotations());
        assertEquals(clazz, d.getTestClass());
        assertEquals("tessed", d.getMethodName());
        assertEquals(1, d.getAnnotations().size());
        assertEquals(Test.class, d.getAnnotations().iterator().next().annotationType());

        d = d.childlessCopy();
        assertEquals(clazz, d.getTestClass());
        assertEquals("tessed", d.getMethodName());
        assertEquals(1, d.getAnnotations().size());
        assertEquals(Test.class, d.getAnnotations().iterator().next().annotationType());
    }

    @Ignore
    private static class Sweet {
        @Test
        public void tessed() {
        }
    }

}
