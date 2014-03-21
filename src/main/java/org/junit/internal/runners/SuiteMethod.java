package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.Test;

/**
 * Runner for use with JUnit 3.8.x-style AllTests classes
 * (those that only implement a static <code>suite()</code>
 * method). For example:
 * <pre>
 * &#064;RunWith(AllTests.class)
 * public class ProductTests {
 *    public static junit.framework.Test suite() {
 *       ...
 *    }
 * }
 * </pre>
 */
public class SuiteMethod extends JUnit38ClassRunner {
    public SuiteMethod(Class<?> clazz) throws Throwable {
        super(testFromSuiteMethod(clazz));
    }

    public static Test testFromSuiteMethod(Class<?> clazz) throws Throwable {
        Method suiteMethod = null;
        Test suite = null;
        try {
            suiteMethod = clazz.getMethod("suite");
            if (!Modifier.isStatic(suiteMethod.getModifiers())) {
                throw new Exception(clazz.getName() + ".suite() must be static");
            }
            suite = (Test) suiteMethod.invoke(null); // static method
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
        return suite;
    }
}
