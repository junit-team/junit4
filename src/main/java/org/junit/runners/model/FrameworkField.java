package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Represents a field on a test class (currently used only for Rules in
 * {@link BlockJUnit4ClassRunner}, but custom runners can make other uses)
 */
public class FrameworkField extends FrameworkMember<FrameworkField> {
	private final Field fField;

	FrameworkField(Field field) {
		fField= field;
	}

	public String getName() {
		return getField().getName();
	}

	@Override
	public Annotation[] getAnnotations() {
		return fField.getAnnotations();
	}

	public boolean isPublic() {
		int modifiers= fField.getModifiers();
		return Modifier.isPublic(modifiers);
	}

	@Override
	public boolean isShadowedBy(FrameworkField otherMember) {
		return otherMember.getName().equals(getName());
	}

	public boolean isStatic() {
		int modifiers= fField.getModifiers();
		return Modifier.isStatic(modifiers);
	}

	/**
	 * @return the underlying java Field
	 */
	public Field getField() {
		return fField;
	}

	/**
	 * @return the underlying Java Field type
	 * @see java.lang.reflect.Field#getType()
	 */
	public Class<?> getType() {
		return fField.getType();
	}

	/**
	 * Attempts to retrieve the value of this field on {@code target}
	 */
	public Object get(Object target) throws IllegalArgumentException, IllegalAccessException {
		return fField.get(target);
	}
}
