package org.junit.internal.matchers;

import java.lang.reflect.Method;

import org.hamcrest.BaseMatcher;
import org.junit.internal.MethodSorter;
import org.checkerframework.common.value.qual.EnsuresMinLenIf;
/**
 * Convenient base class for Matchers that require a non-null value of a specific type.
 * This simply implements the null check, checks the type and then casts.
 *
 * @author Joe Walnes
 * @deprecated Please use {@link org.hamcrest.TypeSafeMatcher}.
 */
@Deprecated
public abstract class TypeSafeMatcher<T> extends BaseMatcher<T> {

    private Class<?> expectedType;

    /**
     * Subclasses should implement this. The item will already have been checked for
     * the specific type and will never be null.
     */
    public abstract boolean matchesSafely(T item);

    protected TypeSafeMatcher() {
        expectedType = findExpectedType(getClass());
    }

    private static Class<?> findExpectedType(Class<?> fromClass) {
        for (Class<?> c = fromClass; c != Object.class; c = c.getSuperclass()) {
            for (Method method : MethodSorter.getDeclaredMethods(c)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (isMatchesSafelyMethod(method, parameterTypes)) {
                    return parameterTypes[0];
                }
            }
        }

        throw new Error("Cannot determine correct type for matchesSafely() method.");
    }

    private static boolean isMatchesSafelyMethod(Method method) {
        return "matchesSafely".equals(method.getName())
                && method.getParameterTypes().length == 1
                && !method.isSynthetic();
    }

    @EnsuresMinLenIf(expression = "#2", result = true, targetValue = 1)
    private static boolean isMatchesSafelyMethod(Method method, Class<?>[] parameterTypes) {
        return "matchesSafely".equals(method.getName())
                && parameterTypes.length == 1
                && !method.isSynthetic();
    }

    protected TypeSafeMatcher(Class<T> expectedType) {
        this.expectedType = expectedType;
    }

    /**
     * Method made final to prevent accidental override.
     * If you need to override this, there's no point on extending TypeSafeMatcher.
     * Instead, extend the {@link BaseMatcher}.
     */
    @SuppressWarnings({"unchecked"})
    public final boolean matches(Object item) {
        return item != null
                && expectedType.isInstance(item)
                && matchesSafely((T) item);
    }
}
