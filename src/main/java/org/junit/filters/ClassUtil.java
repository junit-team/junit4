package org.junit.filters;

class ClassUtil {
    public static String convertFqnToClassPath(String fqn) {
        return fqn.replace(".", "/") + ".class";
    }

    public Class<?> forName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
