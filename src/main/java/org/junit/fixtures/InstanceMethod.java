package org.junit.fixtures;

import java.lang.reflect.Method;

public class InstanceMethod {
    private final Method method;
    private final Object object;
    
    public InstanceMethod(Method method, Object object) {
        this.method = method;
        this.object = object;
    }
    public Object getObject() {
        return object;
    }

    public Method getMethod() {
        return method;
    }
}
