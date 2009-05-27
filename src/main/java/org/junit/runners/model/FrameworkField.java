package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FrameworkField implements FrameworkMember<FrameworkField> {
	private final Field fField;

	public FrameworkField(Field field) {
		fField= field;
	}

	public Annotation[] getAnnotations() {
		return fField.getAnnotations();
	}

	// TODO (May 25, 2009 9:45:43 PM): faking it
	public boolean isShadowedBy(FrameworkField otherMember) {
		// TODO Auto-generated method stub
		return false;
	}

	public Field getField() {
		return fField;
	}

	public Object get(Object target) throws IllegalArgumentException, IllegalAccessException {
		return fField.get(target);
	}
}
