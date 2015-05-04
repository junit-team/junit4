package org.junit.runners.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Can create the arguments for a {@code FrameworkMethod} from an array
 * of Strings. Can also be used to validate that this is possible
 */
public class ArgumentFactory {
    @SuppressWarnings("unchecked")
    private static final Set<? extends Object> KNOWN_TYPES = 
            new HashSet<Class<? extends Object>>(Arrays.asList(
                    String.class, Integer.class, int.class,
                    Double.class, double.class, Boolean.class,
                    boolean.class, Long.class, long.class));
    /**
     * Check that the arguments are convertable for and usable with the given method
     * @param arguments a String array of arguments
     * @param method to check
     * @param errors validation errors list to add to
     */
    public static void validateCanBeUsedWith(final String[] arguments, FrameworkMethod method,
            List<Throwable> errors) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (arguments.length!=parameterTypes.length) {
            errors.add(new Exception("Method " + method.getName() + " expects " +
                    parameterTypes.length + " arguments, cannot be used with " +
                    arguments.length + " arguments"));
        } else {
            for(Class<?> clazz:parameterTypes) {
                if (!canConvertTo(clazz)) {
                    errors.add(new Exception("Cannot call method " + method.getName() +
                            " as cannot convert a String to " + clazz.getCanonicalName()));
                }
            }
        }
        
    }

    private static boolean canConvertTo(Class<?> clazz) {
        return KNOWN_TYPES.contains(clazz);
    }

    /**
     * Convert the string values into the right types for invoking the given test method
     * @param values values to convert
     * @param testMethod method to call
     * @return converted values
     */
    public static Object[] convert(String[] values, FrameworkMethod testMethod) {
        final Class<?>[] parameterTypes = testMethod.getParameterTypes();
        final int numberOfParameters = parameterTypes.length;
        
        Object[] parameters = new Object[numberOfParameters];
        
        for(int i=0; i<numberOfParameters; i++) {
            Class<?> clazz = parameterTypes[i];
            parameters[i] = convertParameter(values[i], clazz);
        }
         
        return parameters;
    }

    private static Object convertParameter(String parameter, Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return parameter;
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return Integer.parseInt(parameter);
        } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return Double.parseDouble(parameter);
        } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return Boolean.parseBoolean(parameter);
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return Long.parseLong(parameter);
        } 
 
        // unknown conversion
        throw new IllegalArgumentException("Cannot convert String argument to " + clazz.toString());
    }
}
