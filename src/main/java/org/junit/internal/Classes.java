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
     * If the current thread does not have a class loader, falls back to the class loader for
     * {@link Classes}.
     *
     * @param className Name of the class.
     */
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return getClass(className, Classes.class);
    }

    /**
     * Returns Class.forName for {@code className} using the current thread's class loader.
     * If the current thread does not have a class loader, falls back to the class loader for the
     * passed-in class.
     *
     * @param className Name of the class.
     * @param callingClass Class that is requesting a the class
     * @since 4.13
     */
    public static Class<?> getClass(String className, Class<?> callingClass) throws ClassNotFoundException {
        ClassLoader classLoader = currentThread().getContextClassLoader();
        return Class.forName(className, true, classLoader == null ? callingClass.getClassLoader() : classLoader);
    }
}
