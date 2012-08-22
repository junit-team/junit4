package org.junit.tests.experimental.theories.extendingwithstubs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodCall {
    private final Method method;
    private final Object[] args;

    public MethodCall(Method method, Object... args) {
        this.method = method;
        this.args = args;
    }

    @Override
    public boolean equals(Object obj) {
        MethodCall call = (MethodCall) obj;
        return call.method.equals(method) && Arrays.deepEquals(call.args, args);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", method.getName(), argListString());
    }

    private String argListString() {
        if (args == null) {
            return null;
        }
        return argList().toString().substring(1, argList().toString().length() - 1);
    }

    private List<Object> argList() {
        ArrayList<Object> list = new ArrayList<Object>();
        for (Object arg : args) {
            list.add(new StringableObject(arg));
        }
        return list;
    }

    public Object stringableObject(Object arg) {
        return new StringableObject(arg).stringableObject();
    }
}
