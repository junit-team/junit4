package org.junit.runner;


/**
 * The {@code TestDescription} describes a test which is to be run or has been run.
 *
 * <p>Until version 4.11 {@code Description} instances were mutable objects. With 4.12 the DescriptionBuilder was
 * introduced that guarantees that all generated descriptions are immutable objects.
 *
 * @see org.junit.runner.Description
 * @see org.junit.runner.ImmutableDescription
 * @see org.junit.runner.DescriptionBuilder
 * @since 4.12
 */
final class TestDescription extends ImmutableDescription {
    private static final long serialVersionUID = 1L;
    private final String methodName;

    /**
     * @param methodName method name (can be null)
     */
    TestDescription(DescriptionBuilder<?> builder, String methodName) {
        super(builder);
        this.methodName = methodName;
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
}