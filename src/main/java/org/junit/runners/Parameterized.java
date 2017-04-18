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
import org.junit.runners.model.TestClass;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParametersFactory;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * The custom runner <code>Parameterized</code> implements parameterized tests.
 * When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * <p>
 * For example, to test the <code>+</code> operator, write:
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class AdditionTest {
 *     &#064;Parameters(name = &quot;{index}: {0} + {1} = {2}&quot;)
 *     public static Iterable&lt;Object[]&gt; data() {
 *         return Arrays.asList(new Object[][] { { 0, 0, 0 }, { 1, 1, 2 },
 *                 { 3, 2, 5 }, { 4, 3, 7 } });
 *     }
 *
 *     private int firstSummand;
 *
 *     private int secondSummand;
 *
 *     private int sum;
 *
 *     public AdditionTest(int firstSummand, int secondSummand, int sum) {
 *         this.firstSummand = firstSummand;
 *         this.secondSummand = secondSummand;
 *         this.sum = sum;
 *     }
 *
 *     &#064;Test
 *     public void test() {
 *         assertEquals(sum, firstSummand + secondSummand);
 *     }
 * }
 * </pre>
 * <p>
 * Each instance of <code>AdditionTest</code> will be constructed using the
 * three-argument constructor and the data values in the
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
 * names like <code>[2: 3 + 2 = 5]</code>. If you don't use the name parameter,
 * then the current parameter index is used as name.
 * <p>
 * You can also write:
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class AdditionTest {
 *     &#064;Parameters(name = &quot;{index}: {0} + {1} = {2}&quot;)
 *     public static Iterable&lt;Object[]&gt; data() {
 *         return Arrays.asList(new Object[][] { { 0, 0, 0 }, { 1, 1, 2 },
 *                 { 3, 2, 5 }, { 4, 3, 7 } });
 *     }
 *
 *     &#064;Parameter(0)
 *     public int firstSummand;
 *
 *     &#064;Parameter(1)
 *     public int secondSummand;
 *
 *     &#064;Parameter(2)
 *     public int sum;
 *
 *     &#064;Test
 *     public void test() {
 *         assertEquals(sum, firstSummand + secondSummand);
 *     }
 * }
 * </pre>
 * <p>
 * Each instance of <code>AdditionTest</code> will be constructed with the default constructor
 * and fields annotated by <code>&#064;Parameter</code>  will be initialized
 * with the data values in the <code>&#064;Parameters</code> method.
 *
 * <p>
 * The parameters can be provided as an array, too:
 * 
 * <pre>
 * &#064;Parameters
 * public static Object[][] data() {
 * 	return new Object[][] { { 0, 0, 0 }, { 1, 1, 2 }, { 3, 2, 5 }, { 4, 3, 7 } } };
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
 * public class YourRunnerFactory implements ParametersRunnerFactory {
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
    public @interface Parameters {
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
    public @interface Parameter {
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

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public Parameterized(Class<?> klass) throws Throwable {
        super(klass, RunnersFactory.createRunnersForClass(klass));
    }

    private static class RunnersFactory {
        private static final ParametersRunnerFactory DEFAULT_FACTORY = new BlockJUnit4ClassRunnerWithParametersFactory();

        private final TestClass testClass;

        private final ParametersRunnerFactory runnerFactory;

        private RunnersFactory(Class<?> klass)
                throws IllegalAccessException, InstantiationException {
            testClass = new TestClass(klass);
            UseParametersRunnerFactory annotation = klass.getAnnotation(
                    UseParametersRunnerFactory.class);
            if (annotation == null) {
                runnerFactory = DEFAULT_FACTORY;
            } else {
                runnerFactory = annotation.value().newInstance();
            }
        }

        static List<Runner> createRunnersForClass(Class<?> klass)
                throws Throwable {
            return new RunnersFactory(klass).createRunners();
        }

        private List<Runner> createRunners() throws Throwable {
            List<Runner> runners = new ArrayList<Runner>();
            for (FrameworkMethod method : getParametersMethods()) {
                runners.addAll(createRunnersForMethod(method));
            }
            return Collections.unmodifiableList(runners);
        }

        private List<FrameworkMethod> getParametersMethods() throws Exception {
            List<FrameworkMethod> methods = testClass.getAnnotatedMethods(
                    Parameters.class);

            if (methods.isEmpty()) {
                throw new Exception("No parameters methods found");
            }

            for (FrameworkMethod method : methods) {
                if (!method.isPublic()) {
                    throw new Exception("Parameters method should be public");
                }

                if (!method.isStatic()) {
                    throw new Exception("Parameters method should be static");
                }
            }
            return methods;
        }

        private List<Runner> createRunnersForMethod(FrameworkMethod method)
                throws Throwable {
            List<Runner> runners = new ArrayList<Runner>();
            for (TestWithParameters test : createTestsForMethod(method)) {
                runners.add(runnerFactory.createRunnerForTestWithParameters(
                        test));
            }
            return runners;
        }

        private List<TestWithParameters> createTestsForMethod(
                FrameworkMethod method)
                throws Throwable {
            String pattern = method.getAnnotation(Parameters.class).name();
            List<TestWithParameters> tests = new ArrayList<TestWithParameters>();
            int index = 0;
            for (Object parameters : getParametersForMethod(method)) {
                String finalPattern = pattern
                        .replaceAll("\\{method\\}", method.getName())
                        .replaceAll("\\{index\\}", Integer.toString(index++));
                Object[] normalizedParameters = normalizeParameters(parameters);
                String name = MessageFormat.format(finalPattern,
                        normalizedParameters);
                tests.add(new TestWithParameters("[" + name + "]", testClass,
                        Arrays.asList(normalizedParameters)));
            }
            return tests;
        }

        @SuppressWarnings("unchecked")
        private Iterable<Object> getParametersForMethod(
                FrameworkMethod method)
                throws Throwable {
            Object parameters = method.invokeExplosively(null);

            try {
                if (parameters instanceof Iterable) {
                    return (Iterable<Object>) parameters;
                }

                if (parameters instanceof Object[]) {
                    return Arrays.asList((Object[]) parameters);
                }
            } catch (ClassCastException ex) {
                // ignore
            }

            String message = MessageFormat.format(
                    "{0}.{1}() must return an Iterable of arrays.",
                    testClass.getName(), method.getName());
            throw new Exception(message);
        }

        private Object[] normalizeParameters(Object parameters) {
            if (parameters instanceof Object[]) {
                return (Object[]) parameters;
            }

            return new Object[]{parameters};
        }
    }
}
