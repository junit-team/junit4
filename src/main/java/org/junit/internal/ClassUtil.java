package org.junit.internal;

import static java.lang.Thread.currentThread;

public class ClassUtil {
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(className, true, currentThread().getContextClassLoader());
    }
}
