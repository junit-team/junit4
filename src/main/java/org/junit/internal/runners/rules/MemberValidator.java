package org.junit.internal.runners.rules;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.runners.model.FrameworkMember;

/**
 * Encapsulates a single piece of validation logic for a field or method.
 *
 * <p>See also {@link AnnotatedMemberValidator}.
 */
interface MemberValidator {
    /**
     * Examine the given member and add any violations of the strategy's validation logic to the given list of errors
     * @param member The member (field or member) to examine
     * @param annotation The type of rule annotation on the member
     * @param errors The list of errors to add validation violations to
     */
    void validate(FrameworkMember<?> member, Class<? extends Annotation> annotation, List<Throwable> errors);
}