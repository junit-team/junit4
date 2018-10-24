package org.junit.runner;

import static org.junit.internal.Checks.notEmpty;
import static org.junit.internal.Checks.notNull;

import java.lang.reflect.Method;
import java.util.List;

public final class MethodBasedDescriptionBuilder extends DescriptionBuilder<MethodBasedDescriptionBuilder> {
    private final Class<?> testClass;
    private final String methodName;
    private final Method method;

    MethodBasedDescriptionBuilder(Class<?> testClass, Method method) {
        super(notNull(method, "method cannot be null").getAnnotations());
        this.testClass = notNull(testClass, "testClass cannot be null");
        this.method = method;
        methodName = method.getName();
        if (!method.getDeclaringClass().isAssignableFrom(testClass)) {
            throw new IllegalArgumentException(
                    "Method [" + method + "] does not exist in class " + testClass.getCanonicalName());
        }
        super.displayName = formatDisplayName(method.getName(), testClass);
        super.uniqueId = displayName;
    }

    MethodBasedDescriptionBuilder(Class<?> testClass, String methodName) {
        super(NO_ANNOTATIONS);
        this.testClass = notNull(testClass, "testClass cannot be null");
        this.methodName = notNull(methodName, "methodName cannot be null");
        this.method = null;
        super.displayName = formatDisplayName(methodName, testClass);
        super.uniqueId = displayName;
    }

    MethodBasedDescriptionBuilder(String testClassName, String methodName) {
        super(NO_ANNOTATIONS);
        notNull(testClassName, "testClassName cannot be null");
        this.methodName = notNull(methodName, "methodName cannot be null");
        this.method = null;
        super.displayName = formatDisplayName(methodName, testClassName);
        super.uniqueId = displayName;
        this.testClass = null;
    }

    /**
     * Create a {@code ImmutableDescription} representing a test for the current state of the {@code DescriptionBuilder}.
     *
     * @return a {@code ImmutableDescription} represented by the {@code DescriptionBuilder}
     */
    public ImmutableDescription createTestDescription() {
        return new TestDescription(this, method, methodName);
    }

    /**
     * Create a {@code ImmutableDescription} representing a suite for the current state of the {@code DescriptionBuilder}.
     *
     * @param children the children of this suite. These usually would have the same class name.
     * @return a {@code ImmutableDescription} represented by the {@code DescriptionBuilder}
     */
    public <T extends ImmutableDescription> ImmutableDescription createSuiteDescription(List<T> children) {
        return new SuiteDescription(this, notNull(children));
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