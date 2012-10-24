package org.junit.runners;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * <p>
 * The custom runner <code>Parameterized</code> implements parameterized tests.
 * When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * </p>
 *
 * For example, to test a Fibonacci function, write:
 *
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class FibonacciTest {
 * 	&#064;Parameters(name= &quot;{index}: fib({0})={1}&quot;)
 * 	public static Iterable&lt;Object[]&gt; data() {
 * 		return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },
 *                 { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
 *     }
 *
 * 	private int fInput;
 *
 * 	private int fExpected;
 *
 * 	public FibonacciTest(int input, int expected) {
 * 		fInput= input;
 * 		fExpected= expected;
 *     }
 *
 * 	&#064;Test
 * 	public void test() {
 * 		assertEquals(fExpected, Fibonacci.compute(fInput));
 *     }
 * }
 * </pre>
 *
 * <p>
 * Each instance of <code>FibonacciTest</code> will be constructed using the
 * two-argument constructor and the data values in the
 * <code>&#064;Parameters</code> method.
 *
 * <p>
 * In order that you can easily identify the individual tests, you may provide a
 * name for the <code>&#064;Parameters</code> annotation. This name is allowed
 * to contain placeholders, which are replaced at runtime. The placeholders are
 * <dl>
 * <dt>{index}</dt>
 * <dd>the current parameter index</dd>
 * <dt>{0}</dt>
 * <dd>the first parameter value</dd>
 * <dt>{1}</dt>
 * <dd>the second parameter value</dd>
 * <dt>...</dt>
 * <dd></dd>
 * </dl>
 * In the example given above, the <code>Parameterized</code> runner creates
 * names like <code>[1: fib(3)=2]</code>. If you don't use the name parameter,
 * then the current parameter index is used as name.
 * </p>
 *
 * You can also write:
 *
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class FibonacciTest {
 *  &#064;Parameters
 *  public static Iterable&lt;Object[]&gt; data() {
 *      return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },
 *                 { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
 *     }
 *  &#064;Parameter(0)
 *  public int fInput;
 *
 *  &#064;Parameter(1)
 *  public int fExpected;
 *
 *  &#064;Test
 *  public void test() {
 *      assertEquals(fExpected, Fibonacci.compute(fInput));
 *     }
 * }
 * </pre>
 *
 * <p>
 * Each instance of <code>FibonacciTest</code> will be constructed with the default constructor
 * and fields annotated by <code>&#064;Parameter</code>  will be initialized
 * with the data values in the <code>&#064;Parameters</code> method.
 * </p>
 *
 * @since 4.0
 */
public class Parameterized extends Suite {
    /**
     * Annotation for a method which provides parameters to be injected into the
     * test class constructor by <code>Parameterized</code>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Parameters {
        /**
         * <p>
         * Optional pattern to derive the test's name from the parameters. Use
         * numbers in braces to refer to the parameters or the additional data
         * as follows:
         * </p>
         *
         * <pre>
         * {index} - the current parameter index
         * {0} - the first parameter value
         * {1} - the second parameter value
         * etc...
         * </pre>
         * <p>
         * Default value is "{index}" for compatibility with previous JUnit
         * versions.
         * </p>
         *
         * @return {@link MessageFormat} pattern string, except the index
         *         placeholder.
         * @see MessageFormat
         */
        String name() default "{index}";
    }

    /**
     * Annotation for fields of the test class which will be initialized by the
     * method annotated by <code>Parameters</code><br/>
     * By using directly this annotation, the test class constructor isn't needed.<br/>
     * Index range must start at 0.
     * Default value is 0.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Parameter {
        /**
         * Method that returns the index of the parameter in the array
         * returned by the method annotated by <code>Parameters</code>.<br/>
         * Index range must start at 0.
         * Default value is 0.
         *
         * @return the index of the parameter.
         */
        int value() default 0;
    }

    private class TestClassRunnerForParameters extends BlockJUnit4ClassRunner {
        private final Object[] fParameters;

        private final String fName;

        TestClassRunnerForParameters(Class<?> type, Object[] parameters,
                String name) throws InitializationError {
            super(type);
            fParameters = parameters;
            fName = name;
        }

        @Override
        public Object createTest() throws Exception {
            if (fieldsAreAnnotated()) {
                return createTestUsingFieldInjection();
            } else {
                return createTestUsingConstructorInjection();
            }
        }

        private Object createTestUsingConstructorInjection() throws Exception {
            return getTestClass().getOnlyConstructor().newInstance(fParameters);
        }

        private Object createTestUsingFieldInjection() throws Exception {
            List<FrameworkField> annotatedFieldsByParameter = getAnnotatedFieldsByParameter();
            if (annotatedFieldsByParameter.size() != fParameters.length) {
                throw new Exception("Wrong number of parameters and @Parameter fields." +
                        " @Parameter fields counted: " + annotatedFieldsByParameter.size() + ", available parameters: " + fParameters.length + ".");
            }
            Object testClassInstance = getTestClass().getJavaClass().newInstance();
            for (FrameworkField each : annotatedFieldsByParameter) {
                Field field = each.getField();
                Parameter annotation = field.getAnnotation(Parameter.class);
                int index = annotation.value();
                try {
                    field.set(testClassInstance, fParameters[index]);
                } catch (IllegalArgumentException iare) {
                    throw new Exception(getTestClass().getName() + ": Trying to set " + field.getName() +
                            " with the value " + fParameters[index] +
                            " that is not the right type (" + fParameters[index].getClass().getSimpleName() + " instead of " +
                            field.getType().getSimpleName() + ").", iare);
                }
            }
            return testClassInstance;
        }

        @Override
        protected String getName() {
            return fName;
        }

        @Override
        protected String testName(FrameworkMethod method) {
            return method.getName() + getName();
        }

        @Override
        protected void validateConstructor(List<Throwable> errors) {
            validateOnlyOneConstructor(errors);
            if (fieldsAreAnnotated()) {
                validateZeroArgConstructor(errors);
            }
        }

        @Override
        protected void validateFields(List<Throwable> errors) {
            super.validateFields(errors);
            if (fieldsAreAnnotated()) {
                List<FrameworkField> annotatedFieldsByParameter = getAnnotatedFieldsByParameter();
                int[] usedIndices = new int[annotatedFieldsByParameter.size()];
                for (FrameworkField each : annotatedFieldsByParameter) {
                    int index = each.getField().getAnnotation(Parameter.class).value();
                    if (index < 0 || index > annotatedFieldsByParameter.size() - 1) {
                        errors.add(
                                new Exception("Invalid @Parameter value: " + index + ". @Parameter fields counted: " +
                                        annotatedFieldsByParameter.size() + ". Please use an index between 0 and " +
                                        (annotatedFieldsByParameter.size() - 1) + ".")
                        );
                    } else {
                        usedIndices[index]++;
                    }
                }
                for (int index = 0; index < usedIndices.length; index++) {
                    int numberOfUse = usedIndices[index];
                    if (numberOfUse == 0) {
                        errors.add(new Exception("@Parameter(" + index + ") is never used."));
                    } else if (numberOfUse > 1) {
                        errors.add(new Exception("@Parameter(" + index + ") is used more than once (" + numberOfUse + ")."));
                    }
                }
            }
        }

        @Override
        protected Statement classBlock(RunNotifier notifier) {
            return childrenInvoker(notifier);
        }

        @Override
        protected Annotation[] getRunnerAnnotations() {
            return new Annotation[0];
        }
    }

    private static final List<Runner> NO_RUNNERS = Collections
            .<Runner>emptyList();

    private final ArrayList<Runner> runners = new ArrayList<Runner>();

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public Parameterized(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);
        Parameters parameters = getParametersMethod().getAnnotation(
                Parameters.class);
        createRunnersForParameters(allParameters(), parameters.name());
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @SuppressWarnings("unchecked")
    private Iterable<Object[]> allParameters() throws Throwable {
        Object parameters = getParametersMethod().invokeExplosively(null);
        if (parameters instanceof Iterable) {
            return (Iterable<Object[]>) parameters;
        } else {
            throw parametersMethodReturnedWrongType();
        }
    }

    private FrameworkMethod getParametersMethod() throws Exception {
        List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(
                Parameters.class);
        for (FrameworkMethod each : methods) {
            if (each.isStatic() && each.isPublic()) {
                return each;
            }
        }

        throw new Exception("No public static parameters method on class "
                + getTestClass().getName());
    }

    private void createRunnersForParameters(Iterable<Object[]> allParameters,
            String namePattern) throws InitializationError, Exception {
        try {
            int i = 0;
            for (Object[] parametersOfSingleTest : allParameters) {
                String name = nameFor(namePattern, i, parametersOfSingleTest);
                TestClassRunnerForParameters runner = new TestClassRunnerForParameters(
                        getTestClass().getJavaClass(), parametersOfSingleTest,
                        name);
                runners.add(runner);
                ++i;
            }
        } catch (ClassCastException e) {
            throw parametersMethodReturnedWrongType();
        }
    }

    private String nameFor(String namePattern, int index, Object[] parameters) {
        String finalPattern = namePattern.replaceAll("\\{index\\}",
                Integer.toString(index));
        String name = MessageFormat.format(finalPattern, parameters);
        return "[" + name + "]";
    }

    private Exception parametersMethodReturnedWrongType() throws Exception {
        String className = getTestClass().getName();
        String methodName = getParametersMethod().getName();
        String message = MessageFormat.format(
                "{0}.{1}() must return an Iterable of arrays.",
                className, methodName);
        return new Exception(message);
    }

    private List<FrameworkField> getAnnotatedFieldsByParameter() {
        return getTestClass().getAnnotatedFields(Parameter.class);
    }

    private boolean fieldsAreAnnotated() {
        return !getAnnotatedFieldsByParameter().isEmpty();
    }
}