package org.junit.runners.model;

/**
 * Represents a receiver for values of annotated fields/methods together with the declaring member.
 *
 * @see TestClass#collectAnnotatedFieldValues(Object, Class, Class, MemberValueConsumer)
 * @see TestClass#collectAnnotatedMethodValues(Object, Class, Class, MemberValueConsumer)
 * @since 4.13
 */
public interface MemberValueConsumer<T> {
    /**
     * Receives the next value and its declaring member.
     *
     * @param member declaring member ({@link FrameworkMethod} or {@link FrameworkField})
     * @param value the value of the next member
     */
    void accept(FrameworkMember<?> member, T value);
}
