package org.junit.experimental.theories;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterSignature {
    
    @SuppressWarnings("serial")
    private static final Map<Class<?>, Class<?>> primitivesBoxingMap = new HashMap<Class<?>, Class<?>>() {{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(char.class, Character.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
    }};
    
    public static ArrayList<ParameterSignature> signatures(Method method) {
        return signatures(method.getParameterTypes(), method
                .getParameterAnnotations());
    }

    public static List<ParameterSignature> signatures(Constructor<?> constructor) {
        return signatures(constructor.getParameterTypes(), constructor
                .getParameterAnnotations());
    }

    private static ArrayList<ParameterSignature> signatures(
            Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
        ArrayList<ParameterSignature> sigs = new ArrayList<ParameterSignature>();
        for (int i = 0; i < parameterTypes.length; i++) {
            sigs.add(new ParameterSignature(parameterTypes[i],
                    parameterAnnotations[i]));
        }
        return sigs;
    }

    private final Class<?> type;

    private final Annotation[] annotations;

    private ParameterSignature(Class<?> type, Annotation[] annotations) {
        this.type = type;
        this.annotations = annotations;
    }

    public boolean canAcceptValue(Object candidate) {
        return (candidate == null) ? !type.isPrimitive() : canAcceptType(candidate.getClass());
    }

    public boolean canAcceptType(Class<?> candidate) {
        return type.isAssignableFrom(candidate) ||
                canAcceptBoxed(candidate) ||
                canAcceptUnboxed(candidate);
    }

    private boolean canAcceptBoxed(Class<?> candidate) {
        if (primitivesBoxingMap.containsKey(candidate)) {
            Class<?> wrapperClass = primitivesBoxingMap.get(candidate);
            return type.isAssignableFrom(wrapperClass);
        } else {
            return false;
        }
    }

    private boolean canAcceptUnboxed(Class<?> candidate) {
        Field primitiveClassField = null;
        try {
            primitiveClassField = candidate.getDeclaredField("TYPE");
        } catch (NoSuchFieldException e) {
            return false;
        }
        
        try {
            if (Modifier.isStatic(primitiveClassField.getModifiers())) {
                Class<?> primitiveClass = (Class<?>) primitiveClassField.get(null);
                return type.isAssignableFrom(primitiveClass);
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	public Class<?> getType() {
        return type;
    }

    public List<Annotation> getAnnotations() {
        return Arrays.asList(annotations);
    }

    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return getAnnotation(type) != null;
    }

    public <T extends Annotation> T findDeepAnnotation(Class<T> annotationType) {
        Annotation[] annotations2 = annotations;
        return findDeepAnnotation(annotations2, annotationType, 3);
    }

    private <T extends Annotation> T findDeepAnnotation(
            Annotation[] annotations, Class<T> annotationType, int depth) {
        if (depth == 0) {
            return null;
        }
        for (Annotation each : annotations) {
            if (annotationType.isInstance(each)) {
                return annotationType.cast(each);
            }
            Annotation candidate = findDeepAnnotation(each.annotationType()
                    .getAnnotations(), annotationType, depth - 1);
            if (candidate != null) {
                return annotationType.cast(candidate);
            }
        }

        return null;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation each : getAnnotations()) {
            if (annotationType.isInstance(each)) {
                return annotationType.cast(each);
            }
        }
        return null;
    }
}