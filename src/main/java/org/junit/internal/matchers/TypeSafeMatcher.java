package org.junit.internal.matchers;

import java.lang.reflect.Method;

import org.hamcrest.BaseMatcher;

/**
 * Convenient base class for Matchers that require a non-null value of a specific type.
 * This simply implements the null check, checks the type and then casts.
 *
 * @author Joe Walnes
 */
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
            for (Method method : c.getDeclaredMethods()) {
                if (isMatchesSafelyMethod(method)) {
                    return method.getParameterTypes()[0];
                }
            }
        }
        
        throw new Error("Cannot determine correct type for matchesSafely() method.");
    }
    
    private static boolean isMatchesSafelyMethod(Method method) {
        return method.getName().equals("matchesSafely") 
            && method.getParameterTypes().length == 1
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
