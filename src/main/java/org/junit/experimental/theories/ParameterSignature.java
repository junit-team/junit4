package org.junit.experimental.theories;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ParameterSignature {
    
    @SuppressWarnings("serial")
    private static final Map<Class<?>, Class<?>> convertableTypesMap = new HashMap<Class<?>, Class<?>>() {{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(char.class, Character.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
        
        // Make all type conversions symmetric        
        Iterable<Entry<Class<?>, Class<?>>> initialEntries = new HashSet<Entry<Class<?>, Class<?>>>(entrySet());
        for (Entry<Class<?>, Class<?>> entry : initialEntries) {
            this.put(entry.getValue(), entry.getKey());
        }
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
                isAssignableViaTypeConversion(type, candidate);
    }
    
    public boolean canPotentiallyAcceptType(Class<?> candidate) {
        return candidate.isAssignableFrom(type) ||
                isAssignableViaTypeConversion(candidate, type) ||
                canAcceptType(candidate);
    }

    private boolean isAssignableViaTypeConversion(Class<?> targetType, Class<?> candidate) {
        if (convertableTypesMap.containsKey(candidate)) {
            Class<?> wrapperClass = convertableTypesMap.get(candidate);
            return targetType.isAssignableFrom(wrapperClass);
        } else {
            return false;
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