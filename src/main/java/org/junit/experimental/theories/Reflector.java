package org.junit.experimental.theories;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public enum Reflector {
    WITHOUT_GENERICS {
        @Override
        public Type[] parameterTypesOf(Constructor<?> constructor) {
            return constructor.getParameterTypes();
        }

        @Override
        public Type[] parameterTypesOf(Method method) {
            return method.getParameterTypes();
        }

        @Override
        public Type typeOf(Field field) {
            return field.getType();
        }
    },
    WITH_GENERICS {
        @Override
        public Type[] parameterTypesOf(Constructor<?> constructor) {
            return constructor.getGenericParameterTypes();
        }

        @Override
        public Type[] parameterTypesOf(Method method) {
            return method.getGenericParameterTypes();
        }

        @Override
        public Type typeOf(Field field) {
            return field.getGenericType();
        }
    };

    public abstract Type[] parameterTypesOf(Constructor<?> constructor);

    public abstract Type[] parameterTypesOf(Method method);

    public abstract Type typeOf(Field field);
}
