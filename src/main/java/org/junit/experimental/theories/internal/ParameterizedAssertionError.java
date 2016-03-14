package org.junit.experimental.theories.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ParameterizedAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    public ParameterizedAssertionError(Throwable targetException,
            String methodName, Object... params) {
        super(String.format("%s(%s)", methodName, join(", ", params)));
        this.initCause(targetException);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ParameterizedAssertionError && toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public static String join(String delimiter, Object... params) {
        return join(delimiter, Arrays.asList(params));
    }

    public static String join(String delimiter, Collection<Object> values) {
        StringBuilder sb = new StringBuilder();
        Iterator<Object> iter = values.iterator();
        while (iter.hasNext()) {
            Object next = iter.next();
            sb.append(stringValueOf(next));
            if (iter.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    private static String stringValueOf(Object next) {
        try {
            return String.valueOf(next);
        } catch (Throwable e) {
            return "[toString failed]";
        }
    }
}