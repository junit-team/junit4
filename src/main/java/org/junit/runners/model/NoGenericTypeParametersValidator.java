package org.junit.runners.model;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

class NoGenericTypeParametersValidator {
	private final Method fMethod;

	NoGenericTypeParametersValidator(Method method) {
		this.fMethod = method;
	}

	void validate(List<Throwable> errors) {
		for (Type each : fMethod.getGenericParameterTypes())
			validateNoTypeParameterOnType(each, errors);
	}

	private void validateNoTypeParameterOnType(Type type, List<Throwable> errors) {
		if (type instanceof TypeVariable<?>) {
			errors.add(new Exception("Method " + fMethod.getName()
					+ "() contains unresolved type variable " + type));
		} else if (type instanceof ParameterizedType)
			validateNoTypeParameterOnParameterizedType((ParameterizedType) type, errors);
		else if (type instanceof WildcardType)
			validateNoTypeParameterOnWildcardType((WildcardType) type, errors);
		else if (type instanceof GenericArrayType)
			validateNoTypeParameterOnGenericArrayType((GenericArrayType) type, errors);
	}

	private void validateNoTypeParameterOnParameterizedType(ParameterizedType parameterized,
			List<Throwable> errors) {
		for (Type each : parameterized.getActualTypeArguments())
			validateNoTypeParameterOnType(each, errors);
	}

	private void validateNoTypeParameterOnWildcardType(WildcardType wildcard,
			List<Throwable> errors) {
		for (Type each : wildcard.getUpperBounds())
		    validateNoTypeParameterOnType(each, errors);
		for (Type each : wildcard.getLowerBounds())
		    validateNoTypeParameterOnType(each, errors);
	}

	private void validateNoTypeParameterOnGenericArrayType(
			GenericArrayType arrayType, List<Throwable> errors) {
		validateNoTypeParameterOnType(arrayType.getGenericComponentType(), errors);
	}
}