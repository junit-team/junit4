package org.junit.filters;

class ClassUtil {
    public static String convertFqnToClassPath(String fqn) {
        return fqn.replace(".", "/") + ".class";
    }
}
