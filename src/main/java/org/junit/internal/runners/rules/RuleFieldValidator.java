package org.junit.internal.runners.rules;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMember;
import org.junit.runners.model.FrameworkMethod;
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

	private final boolean fOnlyStaticFields;
	private final boolean fMethods;

	private RuleFieldValidator(Class<? extends Annotation> annotation,
			boolean methods, boolean onlyStaticFields) {
		this.fAnnotation= annotation;
		this.fOnlyStaticFields= onlyStaticFields;
		this.fMethods= methods;
	}

	/**
	 * Validate the {@link org.junit.runners.model.TestClass} and adds reasons
	 * for rejecting the class to a list of errors.
	 * @param target the {@code TestClass} to validate.
	 * @param errors the list of errors.
	 */
	public void validate(TestClass target, List<Throwable> errors) {
		if (fMethods) {
			List<FrameworkMethod> methods= target.getAnnotatedMethods(fAnnotation);
			for (FrameworkMethod each : methods)
				validateMember(each, errors);
		} else {
			List<FrameworkField> fields= target.getAnnotatedFields(fAnnotation);
			for (FrameworkField each : fields)
				validateMember(each, errors);
		}
	}

	private void validateMember(FrameworkMember<?> field, List<Throwable> errors) {
		optionallyValidateStatic(field, errors);
		validatePublic(field, errors);
		validateTestRuleOrMethodRule(field, errors);
	}

	private void optionallyValidateStatic(FrameworkMember<?> field,
			List<Throwable> errors) {
		if (fOnlyStaticFields && !field.isStatic())
			addError(errors, field, "must be static.");
	}

	private void validatePublic(FrameworkMember<?> field, List<Throwable> errors) {
		if (!field.isPublic())
			addError(errors, field, "must be public.");
	}

	private void validateTestRuleOrMethodRule(FrameworkMember<?> field,
			List<Throwable> errors) {
		if (!isMethodRule(field) && !isTestRule(field))
			addError(errors, field, fMethods ?
					"must return an implementation of MethodRule or TestRule." :
					"must implement MethodRule or TestRule.");
	}

	private boolean isTestRule(FrameworkMember<?> target) {
		return TestRule.class.isAssignableFrom(target.getType());
	}

	@SuppressWarnings("deprecation")
	private boolean isMethodRule(FrameworkMember<?> target) {
		return MethodRule.class.isAssignableFrom(target.getType());
	}

	private void addError(List<Throwable> errors, FrameworkMember<?> field,
			String suffix) {
		String message= "The @" + fAnnotation.getSimpleName() + " '"
				+ field.getName() + "' " + suffix;
		errors.add(new Exception(message));
	}
}
