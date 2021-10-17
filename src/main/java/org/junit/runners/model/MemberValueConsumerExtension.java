package org.junit.runners.model;

/**
 * @see MemberValueConsumer
 * @since 4.14
 */
public interface MemberValueConsumerExtension {
    void acceptMismatchedTypeValue(FrameworkMember<?> member, Object value);
}
