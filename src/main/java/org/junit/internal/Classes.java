package org.junit.internal;

import static java.lang.Thread.currentThread;

/**
 * Miscellaneous functions dealing with classes.
 */
public class Classes {

    /**
     * Do not instantiate.
     * @deprecated will be private soon.
     */
    @Deprecated
    public Classes() {
    }

    /**
     * Returns Class.forName for {@code className} using the current thread's class loader.
     *
     * @param className Name of the class.
     * @throws ClassNotFoundException
     */
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(className, true, currentThread().getContextClassLoader());
    }
}
