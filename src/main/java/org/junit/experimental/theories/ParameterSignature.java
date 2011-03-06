/**
 *
 */
package org.junit.experimental.theories;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javaruntype.type.Types;

public class ParameterSignature {
    public static List<ParameterSignature> signatures(Method method, Reflector reflector) {
        return signatures(reflector.parameterTypesOf(method), method
                .getParameterAnnotations());
    }

    public static List<ParameterSignature> signatures(Constructor<?> constructor, Reflector reflector) {
        return signatures(reflector.parameterTypesOf(constructor), constructor
                .getParameterAnnotations());
    }

    private static List<ParameterSignature> signatures(
            Type[] types, Annotation[][] parameterAnnotations) {
        List<ParameterSignature> sigs= new ArrayList<ParameterSignature>();
        for (int i= 0; i < types.length; i++) {
            sigs.add(new ParameterSignature(types[i],
                    parameterAnnotations[i]));
        }
        return sigs;
    }

    private final Type type;

    private final Annotation[] annotations;

    private ParameterSignature(Type type, Annotation[] annotations) {
        this.type= type;
        this.annotations= annotations;
    }

    public boolean canAcceptType(Type type) {
        return Types.forJavaLangReflectType(this.type).isAssignableFrom(
            Types.forJavaLangReflectType(type));
    }

    public Type getType() {
        return type;
    }

    public List<Annotation> getAnnotations() {
        return Arrays.asList(annotations);
    }

    public boolean canAcceptArrayType(Type type) {
        org.javaruntype.type.Type<?> typeToken = Types.forJavaLangReflectType(type);
        return typeToken.isArray() && canAcceptType(typeToken.getComponentClass());
    }

    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return getAnnotation(type) != null;
    }

    public <T extends Annotation> T findDeepAnnotation(Class<T> annotationType) {
        Annotation[] annotations2= annotations;
        return findDeepAnnotation(annotations2, annotationType, 3);
    }

    private <T extends Annotation> T findDeepAnnotation(
            Annotation[] annotations, Class<T> annotationType, int depth) {
        if (depth == 0)
            return null;
        for (Annotation each : annotations) {
            if (annotationType.isInstance(each))
                return annotationType.cast(each);
            Annotation candidate= findDeepAnnotation(each.annotationType()
                    .getAnnotations(), annotationType, depth - 1);
            if (candidate != null)
                return annotationType.cast(candidate);
        }

        return null;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation each : getAnnotations())
            if (annotationType.isInstance(each))
                return annotationType.cast(each);
        return null;
    }
}