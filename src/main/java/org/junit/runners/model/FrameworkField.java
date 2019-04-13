package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Represents a field on a test class (currently used only for Rules in
 * {@link BlockJUnit4ClassRunner}, but custom runners can make other uses)
 *
 * @since 4.7
 */
public class FrameworkField extends FrameworkMember<FrameworkField> {
    private final Field field;

    FrameworkField(Field field) {
        if (field == null) {
            throw new NullPointerException(
                    "FrameworkField cannot be created without an underlying field.");
        }
        this.field = field;

        if (isPublic()) {
            // This field could be a public field in a package-scope base class
            try {
                field.setAccessible(true);
            } catch (SecurityException  e) {
                // We may get an IllegalAccessException when we try to access the field
            }
        }
    }

    @Override
    public String getName() {
        return getField().getName();
    }

    public Annotation[] getAnnotations() {
        return field.getAnnotations();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return field.getAnnotation(annotationType);
    }

    @Override
    public boolean isShadowedBy(FrameworkField otherMember) {
        return otherMember.getName().equals(getName());
    }

    @Override
    boolean isBridgeMethod() {
        return false;
    }

    @Override
    protected int getModifiers() {
        return field.getModifiers();
    }

    /**
     * @return the underlying java Field
     */
    public Field getField() {
        return field;
    }

    /**
     * @return the underlying Java Field type
     * @see java.lang.reflect.Field#getType()
     */
    @Override
    public Class<?> getType() {
        return field.getType();
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    /**
     * Attempts to retrieve the value of this field on {@code target}
     */
    public Object get(Object target) throws IllegalArgumentException, IllegalAccessException {
        return field.get(target);
    }

    @Override
    public String toString() {
        return field.toString();
    }
}
