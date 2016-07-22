/**
 * 
 */
package org.junit.runners.model;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.runners.Parameterized;

/**
 * @author Peter Wippermann
 *
 */
public class ParameterizedTestClass extends TestClass {

    public ParameterizedTestClass(Class<?> clazz) {
        super(clazz);
    }

    @SuppressWarnings("unchecked")
    public Iterable<Object> allParameters() throws Throwable {
        Object parameters = getParametersMethod().invokeExplosively(null);
        if (parameters instanceof Iterable) {
            return (Iterable<Object>) parameters;
        } else if (parameters instanceof Object[]) {
            return Arrays.asList((Object[]) parameters);
        } else {
            throw parametersMethodReturnedWrongType();
        }
    }

    private FrameworkMethod getParametersMethod() throws Exception {
        List<FrameworkMethod> methods = getAnnotatedMethods(
                Parameterized.Parameters.class);
        for (FrameworkMethod each : methods) {
            if (each.isStatic() && each.isPublic()) {
                return each;
            }
        }

        throw new Exception(
                "No public static parameters method on class " + getName());
    }

    public Exception parametersMethodReturnedWrongType() throws Exception {
        String className = getName();
        String methodName = getParametersMethod().getName();
        String message = MessageFormat.format(
                "{0}.{1}() must return an Iterable of arrays.", className,
                methodName);
        return new Exception(message);
    }

    public static Object[] normalizeParameter(
            Object parametersOrSingleParameter) {
        return (parametersOrSingleParameter instanceof Object[])
                ? (Object[]) parametersOrSingleParameter
                : new Object[] { parametersOrSingleParameter };
    }

    public static String produceTestName(String pattern, int index,
            Object[] parameters) {
        String finalPattern = pattern.replaceAll("\\{index\\}",
                Integer.toString(index));
        return "[" + MessageFormat.format(finalPattern, parameters) + "]";
    }

    public String getNamePatternForParameters() throws Exception {
        Parameterized.Parameters parametersAnnotation = getParametersMethod()
                .getAnnotation(Parameterized.Parameters.class);
        return parametersAnnotation.name();
    }

}
