package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects values by calling public methods in a class. Subclasses can
 * filter which methods are called and transform their values. The values
 * are collected by calling all methods that have a return type that
 * is assignable to the type parameter of this class (for instance, a
 * {@code MethodValueCollector<MethodRule>} will only call methods that
 * are declared to return a value that is an instance of {@code MethodRule}).
 *
 * <p>Subclasses must provide a compile-time value for the type parameter {@code T}.
 *
 * <p>For example, here is an object for collecting {@code TestRule} instances
 * from methods annotated with {@code @Rule} that are declared to return a type
 * that is assignable to {@code TestRule}:
 *
 * <pre>
 * private static MethodValueCollector&lt;TestRule&gt; TEST_RULES_FROM_METHODS
 *     = new MethodValueCollector&lt;TestRule&gt;() {};
 * </pre>
 *
 * @param <T> return value of the method that should be called
 *
 * @since 4.13
 */
public abstract class MethodValueCollector<T> {
    private final Class<T> returnType;

    MethodValueCollector(Class<T> returnType) {
        this.returnType = returnType;
    }

    @SuppressWarnings("unchecked")
    protected MethodValueCollector() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        Type type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        returnType = (Class<T>) type;
    }

    /**
     * Determines whether the result of calling the given method should be included in the output.
     * Subclasses can override this to filter out values. By default, always returns {@code true}.
     */
    protected boolean includeValue(FrameworkMethod method) {
        return true;
    }

    /**
     * Processes the value that came from a method call. Subclasses can override this to
     * wrap the value, to inspect the value or to make method calls on the value.
     * By default, returns the value.
     */
    protected T processValue(FrameworkMethod method, T value) {
        return value;
    }

    /**
     * Calls the given method on the passed-in object (usually a test instance), passing
     * in no parameters. Subclasses can override this to call the method, passing in
     * parameters.
     */
    protected Object callMethod(FrameworkMethod method, Object instance) throws Throwable {
        return method.invokeExplosively(instance);
    }

    /**
     * Collects values by calling all methods on the given object annotated by the given annotation.
     *
     * @return list of collected values.
     */
    public final List<T> getValues(
            TestClass testClass, Object test, Class<? extends Annotation> annotationClass) {
        List<T> results = new ArrayList<T>();
        for (FrameworkMethod each : testClass.getAnnotatedMethods(annotationClass)) {
            try {
                /*
                 * A method annotated with @Rule may return a @TestRule or a @MethodRule,
                 * we cannot call the method to check whether the return type matches our
                 * expectation i.e. subclass of valueClass. If we do that then the method 
                 * will be invoked twice and we do not want to do that. So we first check
                 * whether return type matches our expectation and only then call the method
                 * to fetch the MethodRule.
                 */
                if (returnType.isAssignableFrom(each.getReturnType()) && includeValue(each)) {
                    Object returnValue = callMethod(each, test);
                    results.add(processValue(each, returnType.cast(returnValue)));
                }
            } catch (Throwable e) {
                throw new RuntimeException("Exception in " + each.getName(), e);
            }
        }
        return results;
    }
}
