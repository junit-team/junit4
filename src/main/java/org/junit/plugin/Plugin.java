package org.junit.plugin;

import java.lang.reflect.Method;

public interface Plugin {

    void prepareTest(Object testInstance, Method testMethod);
}
