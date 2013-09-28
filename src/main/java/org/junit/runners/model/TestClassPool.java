package org.junit.runners.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This pool of {@code TestClass}es is used to reduce the expensive look-ups
 * and reflection calls that are performed during the construction of the
 * {@code TestClass} instances. The pool will keep a reference to each
 * instance so that no {@code TestClass} has to be constructed twice.
 *
 * @since 4.12
 */
public class TestClassPool {
    public static final Map<Class<?>, TestClass> testClasses =
            Collections.synchronizedMap(new HashMap<Class<?>, TestClass>());

    /**
     * Returns a {@code TestClass} wrapping {@code klass}. {@code TestClass}es
     * will be shared and held within a pool, to avoid the expensive process
     * that is performed during construction.
     */
    public static TestClass forClass(Class<?> klass) {
        if (!testClasses.containsKey(klass))
            testClasses.put(klass, new TestClass(klass));
        return testClasses.get(klass);
    }
}