package org.junit.internal.runners.rules;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMember;

/**
 * A RuleMemberValidator validates the rule fields/methods of a
 * {@link org.junit.runners.model.TestClass}. All reasons for rejecting the
 * {@code TestClass} are written to a list of errors.
 *
 * <p>There are four slightly different validators. The {@link #CLASS_RULE_VALIDATOR}
 * validates fields with a {@link ClassRule} annotation and the
 * {@link #RULE_VALIDATOR} validates fields with a {@link Rule} annotation.</p>
 *
 * <p>The {@link #CLASS_RULE_METHOD_VALIDATOR}
 * validates methods with a {@link ClassRule} annotation and the
 * {@link #RULE_METHOD_VALIDATOR} validates methods with a {@link Rule} annotation.</p>
 */
public class RuleMemberValidator extends AnnotatedMemberValidator {
    /**
     * Validates fields with a {@link ClassRule} annotation.
     */
    public static final RuleMemberValidator CLASS_RULE_VALIDATOR =
            classMemberValidatorBuilder()
            .withValidator(new FieldMustBeATestRule())
            .build();
    /**
     * Validates fields with a {@link Rule} annotation.
     */
    public static final RuleMemberValidator RULE_VALIDATOR =
            testMemberValidatorBuilder()
            .withValidator(new MemberMustBeNonStaticOrAlsoClassRule())
            .withValidator(new FieldMustBeARule())
            .build();
    /**
     * Validates methods with a {@link ClassRule} annotation.
     */
    public static final RuleMemberValidator CLASS_RULE_METHOD_VALIDATOR =
            classMemberValidatorBuilder()
            .forMethods()
            .withValidator(new MethodMustBeATestRule())
            .build();

    /**
     * Validates methods with a {@link Rule} annotation.
     */
    public static final RuleMemberValidator RULE_METHOD_VALIDATOR =
            testMemberValidatorBuilder()
            .forMethods()
            .withValidator(new MemberMustBeNonStaticOrAlsoClassRule())
            .withValidator(new MethodMustBeARule())
            .build();

    RuleMemberValidator(Builder builder) {
        super(builder);
    }

    private static Builder classMemberValidatorBuilder() {
        return new Builder().forClassAnnotation(ClassRule.class);
    }

    private static Builder testMemberValidatorBuilder() {
        return new Builder().forTestAnnotation(Rule.class);
    }

    private static class Builder extends AnnotatedMemberValidator.Builder<Builder> {

        RuleMemberValidator build() {
            return new RuleMemberValidator(this);
        }
    }

    private static boolean isTestMethodRuleType(FrameworkMember<?> member) {
        return isMethodRule(member) || isTestRule(member);
    }

    private static boolean isClassRuleType(FrameworkMember<?> member) {
        return isTestRule(member);
    }

    private static boolean isTestRule(FrameworkMember<?> member) {
        return TestRule.class.isAssignableFrom(member.getType());
    }

    private static boolean isMethodRule(FrameworkMember<?> member) {
        return MethodRule.class.isAssignableFrom(member.getType());
    }

    /**
     * Requires the validated member to be non-static
     */
    private static final class MemberMustBeNonStaticOrAlsoClassRule implements MemberValidator {
        public void validate(FrameworkMember<?> member, Class<? extends Annotation> annotation, List<Throwable> errors) {
            boolean isMethodRuleMember = isMethodRule(member);
            boolean isClassRuleAnnotated = (member.getAnnotation(ClassRule.class) != null);

            // We disallow:
            //  - static MethodRule members
            //  - static @Rule annotated members
            //    - UNLESS they're also @ClassRule annotated
            // Note that MethodRule cannot be annotated with @ClassRule
            if (member.isStatic() && (isMethodRuleMember || !isClassRuleAnnotated)) {
                String message;
                if (isMethodRule(member)) {
                    message = "must not be static.";
                } else {
                    message = "must not be static or it must be annotated with @ClassRule.";
                }
                errors.add(new ValidationError(member, annotation, message));
            }
        }
    }

    /**
     * Requires the member is a field implementing {@link org.junit.rules.MethodRule} or {@link org.junit.rules.TestRule}
     */
    private static final class FieldMustBeARule implements MemberValidator {
        public void validate(FrameworkMember<?> member, Class<? extends Annotation> annotation, List<Throwable> errors) {
            if (!isTestMethodRuleType(member)) {
                errors.add(new ValidationError(member, annotation,
                        "must implement MethodRule or TestRule."));
            }
        }
    }

    /**
     * Require the member to return an implementation of {@link org.junit.rules.MethodRule} or
     * {@link org.junit.rules.TestRule}
     */
    private static final class MethodMustBeARule implements MemberValidator {
        public void validate(FrameworkMember<?> member, Class<? extends Annotation> annotation, List<Throwable> errors) {
            if (!isTestMethodRuleType(member)) {
                errors.add(new ValidationError(member, annotation,
                        "must return an implementation of MethodRule or TestRule."));
            }
        }
    }
    
    /**
     * Require the member to return an implementation of {@link org.junit.rules.TestRule}
     */
    private static final class MethodMustBeATestRule implements MemberValidator {
        public void validate(FrameworkMember<?> member,
                Class<? extends Annotation> annotation, List<Throwable> errors) {
            if (!isClassRuleType(member)) {
                errors.add(new ValidationError(member, annotation, 
                        "must return an implementation of TestRule."));
            }
        }
    }
    
    /**
     * Requires the member is a field implementing {@link org.junit.rules.TestRule}
     */
    private static final class FieldMustBeATestRule implements MemberValidator {

        public void validate(FrameworkMember<?> member,
                Class<? extends Annotation> annotation, List<Throwable> errors) {
            if (!isClassRuleType(member)) {
                errors.add(new ValidationError(member, annotation,
                        "must implement TestRule."));
            }
        }
    }
}
