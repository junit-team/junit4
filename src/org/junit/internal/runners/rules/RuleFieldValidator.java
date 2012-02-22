package org.junit.internal.runners.rules;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.TestClass;

/**
 * A RuleFieldValidator validates the rule fields of a
 * {@link org.junit.runners.model.TestClass}. All reasons for rejecting the
 * {@code TestClass} are written to a list of errors.
 * 
 * There are two slightly different validators. The {@link #CLASS_RULE_VALIDATOR}
 * validates fields with a {@link ClassRule} annotation and the
 * {@link #RULE_VALIDATOR} validates fields with a {@link Rule} annotation.
 */
public enum RuleFieldValidator {
	/**
	 * Validates fields with a {@link ClassRule} annotation.
	 */
	CLASS_RULE_VALIDATOR(ClassRule.class, true),
	/**
	 * Validates fields with a {@link Rule} annotation.
	 */
	RULE_VALIDATOR(Rule.class, false);

	private final Class<? extends Annotation> fAnnotation;

	private final boolean fOnlyStaticFields;

	private RuleFieldValidator(Class<? extends Annotation> annotation,
			boolean onlyStaticFields) {
		this.fAnnotation= annotation;
		this.fOnlyStaticFields= onlyStaticFields;
	}

	/**
	 * Validate the {@link org.junit.runners.model.TestClass} and adds reasons
	 * for rejecting the class to a list of errors.
	 * @param target the {@code TestClass} to validate.
	 * @param errors the list of errors.
	 */
	public void validate(TestClass target, List<Throwable> errors) {
		List<FrameworkField> fields= target.getAnnotatedFields(fAnnotation);
		for (FrameworkField each : fields)
			validateField(each, errors);
	}

	private void validateField(FrameworkField field, List<Throwable> errors) {
		optionallyValidateStatic(field, errors);
		validatePublic(field, errors);
		validateTestRuleOrMethodRule(field, errors);
	}

	private void optionallyValidateStatic(FrameworkField field,
			List<Throwable> errors) {
		if (fOnlyStaticFields && !field.isStatic())
			addError(errors, field, "must be static.");
	}

	private void validatePublic(FrameworkField field, List<Throwable> errors) {
		if (!field.isPublic())
			addError(errors, field, "must be public.");
	}

	private void validateTestRuleOrMethodRule(FrameworkField field,
			List<Throwable> errors) {
		if (!isMethodRule(field) && !isTestRule(field))
			addError(errors, field, "must implement MethodRule or TestRule.");
	}

	private boolean isTestRule(FrameworkField target) {
		return TestRule.class.isAssignableFrom(target.getType());
	}

	@SuppressWarnings("deprecation")
	private boolean isMethodRule(FrameworkField target) {
		return org.junit.rules.MethodRule.class.isAssignableFrom(target
				.getType());
	}

	private void addError(List<Throwable> errors, FrameworkField field,
			String suffix) {
		String message= "The @" + fAnnotation.getSimpleName() + " '"
				+ field.getName() + "' " + suffix;
		errors.add(new Exception(message));
	}
}
