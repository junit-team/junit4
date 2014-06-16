package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParametersFactory;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * The custom runner <code>Parameterized</code> implements parameterized tests.
 * When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * <p>
 * For example, to test a Fibonacci function, write:
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class FibonacciTest {
 *     &#064;Parameters(name= &quot;{index}: fib[{0}]={1}&quot;)
 *     public static Iterable&lt;Object[]&gt; data() {
 *         return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },
 *                 { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
 *     }
 *
 *     private int fInput;
 *
 *     private int fExpected;
 *
 *     public FibonacciTest(int input, int expected) {
 *         fInput= input;
 *         fExpected= expected;
 *     }
 *
 *     &#064;Test
 *     public void test() {
 *         assertEquals(fExpected, Fibonacci.compute(fInput));
 *     }
 * }
 * </pre>
 * <p>
 * Each instance of <code>FibonacciTest</code> will be constructed using the
 * two-argument constructor and the data values in the
 * <code>&#064;Parameters</code> method.
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
 * <dd>...</dd>
 * </dl>
 * <p>
 * In the example given above, the <code>Parameterized</code> runner creates
 * names like <code>[1: fib(3)=2]</code>. If you don't use the name parameter,
 * then the current parameter index is used as name.
 * <p>
 * You can also write:
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class FibonacciTest {
 *  &#064;Parameters
 *  public static Iterable&lt;Object[]&gt; data() {
 *      return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },
 *                 { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
 *  }
 *  
 *  &#064;Parameter(0)
 *  public int fInput;
 *
 *  &#064;Parameter(1)
 *  public int fExpected;
 *
 *  &#064;Test
 *  public void test() {
 *      assertEquals(fExpected, Fibonacci.compute(fInput));
 *  }
 * }
 * </pre>
 * <p>
 * Each instance of <code>FibonacciTest</code> will be constructed with the default constructor
 * and fields annotated by <code>&#064;Parameter</code>  will be initialized
 * with the data values in the <code>&#064;Parameters</code> method.
 *
 * <p>
 * The parameters can be provided as an array, too:
 * 
 * <pre>
 * &#064;Parameters
 * public static Object[][] data() {
 * 	return new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 },
 * 			{ 5, 5 }, { 6, 8 } };
 * }
 * </pre>
 * 
 * <h3>Tests with single parameter</h3>
 * <p>
 * If your test needs a single parameter only, you don't have to wrap it with an
 * array. Instead you can provide an <code>Iterable</code> or an array of
 * objects.
 * <pre>
 * &#064;Parameters
 * public static Iterable&lt;? extends Object&gt; data() {
 * 	return Arrays.asList(&quot;first test&quot;, &quot;second test&quot;);
 * }
 * </pre>
 * <p>
 * or
 * <pre>
 * &#064;Parameters
 * public static Object[] data() {
 * 	return new Object[] { &quot;first test&quot;, &quot;second test&quot; };
 * }
 * </pre>
 *
 * <h3>Create different runners</h3>
 * <p>
 * By default the {@code Parameterized} runner creates a slightly modified
 * {@link BlockJUnit4ClassRunner} for each set of parameters. You can build an
 * own {@code Parameterized} runner that creates another runner for each set of
 * parameters. Therefore you have to build a {@link ParametersRunnerFactory}
 * that creates a runner for each {@link TestWithParameters}. (
 * {@code TestWithParameters} are bundling the parameters and the test name.)
 * The factory must have a public zero-arg constructor.
 *
 * <pre>
 * public class YourRunnerFactory implements ParameterizedRunnerFactory {
 *     public Runner createRunnerForTestWithParameters(TestWithParameters test)
 *             throws InitializationError {
 *         return YourRunner(test);
 *     }
 * }
 * </pre>
 * <p>
 * Use the {@link UseParametersRunnerFactory} to tell the {@code Parameterized}
 * runner that it should use your factory.
 *
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * &#064;UseParametersRunnerFactory(YourRunnerFactory.class)
 * public class YourTest {
 *     ...
 * }
 * </pre>
 *
 * @since 4.0
 */
public class Parameterized extends Suite {
    /**
     * Annotation for a method which provides parameters to be injected into the
     * test class constructor by <code>Parameterized</code>. The method has to
     * be public and static.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Parameters {
        /**
         * Optional pattern to derive the test's name from the parameters. Use
         * numbers in braces to refer to the parameters or the additional data
         * as follows:
         * <pre>
         * {index} - the current parameter index
         * {0} - the first parameter value
         * {1} - the second parameter value
         * etc...
         * </pre>
         * <p>
         * Default value is "{index}" for compatibility with previous JUnit
         * versions.
         *
         * @return {@link MessageFormat} pattern string, except the index
         *         placeholder.
         * @see MessageFormat
         */
        String name() default "{index}";
    }

    /**
     * Annotation for fields of the test class which will be initialized by the
     * method annotated by <code>Parameters</code>.
     * By using directly this annotation, the test class constructor isn't needed.
     * Index range must start at 0.
     * Default value is 0.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Parameter {
        /**
         * Method that returns the index of the parameter in the array
         * returned by the method annotated by <code>Parameters</code>.
         * Index range must start at 0.
         * Default value is 0.
         *
         * @return the index of the parameter.
         */
        int value() default 0;
    }

    /**
     * Add this annotation to your test class if you want to generate a special
     * runner. You have to specify a {@link ParametersRunnerFactory} class that
     * creates such runners. The factory must have a public zero-arg
     * constructor.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Target(ElementType.TYPE)
    public @interface UseParametersRunnerFactory {
        /**
         * @return a {@link ParametersRunnerFactory} class (must have a default
         *         constructor)
         */
        Class<? extends ParametersRunnerFactory> value() default BlockJUnit4ClassRunnerWithParametersFactory.class;
    }

    private static final ParametersRunnerFactory DEFAULT_FACTORY = new BlockJUnit4ClassRunnerWithParametersFactory();

    private static final List<Runner> NO_RUNNERS = Collections.<Runner>emptyList();

    private final List<Runner> runners;

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public Parameterized(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);
        ParametersRunnerFactory runnerFactory = getParametersRunnerFactory(
                klass);
        Parameters parameters = getParametersMethod().getAnnotation(
                Parameters.class);
        runners = Collections.unmodifiableList(createRunnersForParameters(
                allParameters(), parameters.name(), runnerFactory));
    }

    private ParametersRunnerFactory getParametersRunnerFactory(Class<?> klass)
            throws InstantiationException, IllegalAccessException {
        UseParametersRunnerFactory annotation = klass
                .getAnnotation(UseParametersRunnerFactory.class);
        if (annotation == null) {
            return DEFAULT_FACTORY;
        } else {
            Class<? extends ParametersRunnerFactory> factoryClass = annotation
                    .value();
            return factoryClass.newInstance();
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    private TestWithParameters createTestWithNotNormalizedParameters(
            String pattern, int index, Object parametersOrSingleParameter) {
        Object[] parameters= (parametersOrSingleParameter instanceof Object[]) ? (Object[]) parametersOrSingleParameter
            : new Object[] { parametersOrSingleParameter };
        return createTestWithParameters(getTestClass(), pattern, index,
                parameters);
    }

    @SuppressWarnings("unchecked")
    private Iterable<Object> allParameters() throws Throwable {
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

    private List<Runner> createRunnersForParameters(
            Iterable<Object> allParameters, String namePattern,
            ParametersRunnerFactory runnerFactory)
            throws InitializationError,
            Exception {
        try {
            List<TestWithParameters> tests = createTestsForParameters(
                    allParameters, namePattern);
            List<Runner> runners = new ArrayList<Runner>();
            for (TestWithParameters test : tests) {
                runners.add(runnerFactory
                        .createRunnerForTestWithParameters(test));
            }
            return runners;
        } catch (ClassCastException e) {
            throw parametersMethodReturnedWrongType();
        }
    }

    private List<TestWithParameters> createTestsForParameters(
            Iterable<Object> allParameters, String namePattern)
            throws Exception {
        int i = 0;
        List<TestWithParameters> children = new ArrayList<TestWithParameters>();
        for (Object parametersOfSingleTest : allParameters) {
            children.add(createTestWithNotNormalizedParameters(namePattern,
                    i++, parametersOfSingleTest));
        }
        return children;
    }

    private Exception parametersMethodReturnedWrongType() throws Exception {
        String className = getTestClass().getName();
        String methodName = getParametersMethod().getName();
        String message = MessageFormat.format(
                "{0}.{1}() must return an Iterable of arrays.",
                className, methodName);
        return new Exception(message);
    }

    private static TestWithParameters createTestWithParameters(
            TestClass testClass, String pattern, int index, Object[] parameters) {
        String finalPattern = pattern.replaceAll("\\{index\\}",
                Integer.toString(index));
        String name = MessageFormat.format(finalPattern, parameters);
        return new TestWithParameters("[" + name + "]", testClass,
                Arrays.asList(parameters));
    }
}
