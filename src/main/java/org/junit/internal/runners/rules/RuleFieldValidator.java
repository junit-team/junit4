package org.junit.internal.runners.rules;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMember;
import org.junit.runners.model.TestClass;

/**
 * A RuleFieldValidator validates the rule fields of a
 * {@link org.junit.runners.model.TestClass}. All reasons for rejecting the
 * {@code TestClass} are written to a list of errors.
 *
 * There are four slightly different validators. The {@link #CLASS_RULE_VALIDATOR}
 * validates fields with a {@link ClassRule} annotation and the
 * {@link #RULE_VALIDATOR} validates fields with a {@link Rule} annotation.
 *
 * The {@link #CLASS_RULE_METHOD_VALIDATOR}
 * validates methods with a {@link ClassRule} annotation and the
 * {@link #RULE_METHOD_VALIDATOR} validates methods with a {@link Rule} annotation.
 */
public enum RuleFieldValidator {
    /**
     * Validates fields with a {@link ClassRule} annotation.
     */
    CLASS_RULE_VALIDATOR(ClassRule.class, false, true),
    /**
     * Validates fields with a {@link Rule} annotation.
     */
    RULE_VALIDATOR(Rule.class, false, false),
    /**
     * Validates methods with a {@link ClassRule} annotation.
     */
    CLASS_RULE_METHOD_VALIDATOR(ClassRule.class, true, true),
    /**
     * Validates methods with a {@link Rule} annotation.
     */
    RULE_METHOD_VALIDATOR(Rule.class, true, false);

    private final Class<? extends Annotation> fAnnotation;

    private final boolean fStaticMembers;
    private final boolean fMethods;

    private RuleFieldValidator(Class<? extends Annotation> annotation,
            boolean methods, boolean fStaticMembers) {
        this.fAnnotation = annotation;
        this.fStaticMembers = fStaticMembers;
        this.fMethods = methods;
    }

    /**
     * Validate the {@link org.junit.runners.model.TestClass} and adds reasons
     * for rejecting the class to a list of errors.
     *
     * @param target the {@code TestClass} to validate.
     * @param errors the list of errors.
     */
    public void validate(TestClass target, List<Throwable> errors) {
        List<? extends FrameworkMember<?>> members = fMethods ? target.getAnnotatedMethods(fAnnotation)
                : target.getAnnotatedFields(fAnnotation);

        for (FrameworkMember<?> each : members) {
            validateMember(each, errors);
        }
    }

    private void validateMember(FrameworkMember<?> member, List<Throwable> errors) {
        validateStatic(member, errors);
        validatePublic(member, errors);
        validateTestRuleOrMethodRule(member, errors);
    }

    private void validateStatic(FrameworkMember<?> member,
            List<Throwable> errors) {
        if (fStaticMembers && !member.isStatic()) {
            addError(errors, member, "must be static.");
        }
        if (!fStaticMembers && member.isStatic()) {
            addError(errors, member, "must not be static.");
        }
    }

    private void validatePublic(FrameworkMember<?> member, List<Throwable> errors) {
        if (!member.isPublic()) {
            addError(errors, member, "must be public.");
        }
    }

    private void validateTestRuleOrMethodRule(FrameworkMember<?> member,
            List<Throwable> errors) {
        if (!isMethodRule(member) && !isTestRule(member)) {
            addError(errors, member, fMethods ?
                    "must return an implementation of MethodRule or TestRule." :
                    "must implement MethodRule or TestRule.");
        }
    }

    private boolean isTestRule(FrameworkMember<?> member) {
        return TestRule.class.isAssignableFrom(member.getType());
    }

    @SuppressWarnings("deprecation")
    private boolean isMethodRule(FrameworkMember<?> member) {
        return MethodRule.class.isAssignableFrom(member.getType());
    }

    private void addError(List<Throwable> errors, FrameworkMember<?> member,
            String suffix) {
        String message = "The @" + fAnnotation.getSimpleName() + " '"
                + member.getName() + "' " + suffix;
        errors.add(new Exception(message));
    }
}
