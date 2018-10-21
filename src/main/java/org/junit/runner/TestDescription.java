package org.junit.runner;

import java.lang.reflect.Method;


/**
 * The {@code TestDescription} describes a test which is to be run or has been run.
 *
 * <p>Until version 4.13 {@code Description} instances were mutable objects. With 4.32 the DescriptionBuilder was
 * introduced that guarantees that all generated descriptions are immutable objects.
 *
 * @see org.junit.runner.Description
 * @see org.junit.runner.ImmutableDescription
 * @see org.junit.runner.DescriptionBuilder
 * @since 4.13
 */
final class TestDescription extends ImmutableDescription {
    private static final long serialVersionUID = 1L;
    private final String methodName;
    private final Method method;

    /**
     * @param methodName method name (can be null)
     */
    TestDescription(DescriptionBuilder<?> builder, Method method, String methodName) {
        super(builder);
        this.methodName = methodName;
        this.method = method;
    }

    @Override
    public boolean isSuite() {
        return false;
    }

    @Override
    public boolean isTest() {
        return true;
    }

    @Override
    public int testCount() {
        return 1;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Method getMethod() {
        return method;
    }
}