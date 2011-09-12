package org.junit.internal.runners.rules;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

/**
 * A RuleFieldValidator validates the rule methods of a
 * {@link org.junit.runners.model.TestClass}. All reasons for rejecting the
 * {@code TestClass} are written to a list of errors.
 * 
 * There are two slightly different validators. The {@link #CLASS_RULE_METHOD_VALIDATOR}
 * validates methods with a {@link ClassRule} annotation and the
 * {@link #RULE_METHOD_VALIDATOR} validates methods with a {@link Rule} annotation.
 */
public enum RuleMethodValidator {
	/**
	 * Validates methods with a {@link ClassRule} annotation.
	 */
	CLASS_RULE_METHOD_VALIDATOR(ClassRule.class, true),
	/**
	 * Validates methods with a {@link Rule} annotation.
	 */
	RULE_METHOD_VALIDATOR(Rule.class, false);

	private final Class<? extends Annotation> fAnnotation;

	private final boolean fOnlyStaticFields;

	private RuleMethodValidator(Class<? extends Annotation> annotation,
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
		List<FrameworkMethod> methods = target.getAnnotatedMethods(fAnnotation);
		for (FrameworkMethod each : methods)
			validateMethod(each, errors);
	}

	private void validateMethod(FrameworkMethod method, List<Throwable> errors) {
		optionallyValidateStatic(method, errors);
		validatePublic(method, errors);
		validateTestRuleOrMethodRule(method, errors);
	}

	private void optionallyValidateStatic(FrameworkMethod method,
			List<Throwable> errors) {
		if (fOnlyStaticFields && !method.isStatic())
			addError(errors, method, "must be static.");
	}

	private void validatePublic(FrameworkMethod method, List<Throwable> errors) {
		if (!method.isPublic())
			addError(errors, method, "must be public.");
	}

	private void validateTestRuleOrMethodRule(FrameworkMethod method,
			List<Throwable> errors) {
		if (!isMethodRule(method) && !isTestRule(method))
			addError(errors, method, "must return an implementation of MethodRule or TestRule.");
	}

	private boolean isTestRule(FrameworkMethod target) {
		return TestRule.class.isAssignableFrom(target.getReturnType());
	}

	@SuppressWarnings("deprecation")
	private boolean isMethodRule(FrameworkMethod target) {
		return MethodRule.class.isAssignableFrom(target.getReturnType());
	}

	private void addError(List<Throwable> errors, FrameworkMethod field,
			String suffix) {
		String message= "The @" + fAnnotation.getSimpleName() + " '"
				+ field.getName() + "' " + suffix;
		errors.add(new Exception(message));
	}
}
