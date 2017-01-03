package org.junit.runner;

import static org.junit.internal.Checks.notEmpty;
import static org.junit.internal.Checks.notNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public final class MethodBasedDescriptionBuilder extends DescriptionBuilder<MethodBasedDescriptionBuilder> {
    private final Class<?> testClass;
    private final String methodName;
    private final Method method;

    MethodBasedDescriptionBuilder(Class<?> testClass, Method method) {
        this.testClass = notNull(testClass, "testClass cannot be null");
        this.method = notNull(method, "method cannot be null");
        methodName = method.getName();
        if (!method.getDeclaringClass().isAssignableFrom(testClass)) {
            throw new IllegalArgumentException(
                    "Method [" + method + "] does not exist in class " + testClass.getCanonicalName());
        }
        super.displayName = formatDisplayName(method.getName(), testClass);
        super.uniqueId = displayName;
        super.annotations = new ArrayList<Annotation>(Arrays.asList(method.getAnnotations()));
    }

    MethodBasedDescriptionBuilder(Class<?> testClass, String methodName) {
        this.testClass = notNull(testClass, "testClass cannot be null");
        this.methodName = notNull(methodName, "methodName cannot be null");
        this.method = null;
        super.displayName = formatDisplayName(methodName, testClass);
        super.uniqueId = displayName;
        super.annotations = new ArrayList<Annotation>();
    }

    MethodBasedDescriptionBuilder(String testClassName, String methodName) {
        notNull(testClassName, "testClassName cannot be null");
        this.methodName = notNull(methodName, "methodName cannot be null");
        this.method = null;
        super.displayName = formatDisplayName(methodName, testClassName);
        super.uniqueId = displayName;
        super.annotations = new ArrayList<Annotation>();
        this.testClass = null;
    }

    public MethodBasedDescriptionBuilder setTestName(String testName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Create a {@code ImmutableDescription} representing a test for the current state of the {@code DescriptionBuilder}.
     *
     * @return a {@code ImmutableDescription} represented by the {@code DescriptionBuilder}
     */
    public ImmutableDescription createTestDescription() {
        return new TestDescription(this, method, methodName);
    }

    @Override
    Class<?> getTestClass() {
        return testClass;
    }
 
    private static String formatDisplayName(String methodName, Class<?> testClass) {
        notEmpty(methodName, "methodName cannot be empty");
        return String.format("%s(%s)", methodName, testClass.getName());
    }

    private static String formatDisplayName(String methodName, String testClassName) {
        notEmpty(methodName, "methodName cannot be empty");
        notEmpty(testClassName, "testClassName cannot be empty");
        return String.format("%s(%s)", methodName, testClassName);
    }
}