package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Represents a field on a test class (currently used only for Interceptors in
 * {@link BlockJUnit4ClassRunner}, but custom runners can make other uses)
 */
public class FrameworkField extends FrameworkMember<FrameworkField> {
	private final Field fField;

	public FrameworkField(Field field) {
		fField= field;
	}

	@Override
	public Annotation[] getAnnotations() {
		return fField.getAnnotations();
	}

	@Override
	public boolean isShadowedBy(FrameworkField otherMember) {
		return otherMember.getField().getName().equals(getField().getName());
	}

	public Field getField() {
		return fField;
	}

	public Object get(Object target) throws IllegalArgumentException, IllegalAccessException {
		return fField.get(target);
	}
}
