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
 * For example, this class has a method that adds two integers and returns the sum:
 * <pre>
 * &#047;* This is an example class that adds two integers. *&#047;
 * public class IntAdder {
 *     public static int add(int addend1, int addend2) {
 *         return addend1 + addend2;
 *     }
 * }
 * </pre>
 * The following is a test for {&#064;link IntAdder}.
 * For each element returned from the <code>&#064;Parameters</code> annotated method,
 * an instance of the <code>Parameterized</code> test class is created and
 * its <code>&#064;Test</code> annotated method (e.g. <code>test()</code>) is invoked.
 * <pre>
 * &#047;* This is is the unit test for {&#064;link IntAdder}. *&#047;
 * &#064;RunWith(Parameterized.class)
 * public class IntAdderTest {
 *     &#047;* Each of these is used to create a new {&#064;link IntAdderTest} instance. *&#047;
 *     &#064;Parameters(name = &quot;{index}: {0} + {1} = {2}&quot;)
 *     public static Iterable&lt;Object[]&gt; data() {
 *         return Arrays.asList(new Object[][] {
 *             { -1, 1, 0 },
 *             {  0, 0, 0 },
 *             {  1, 0, 1 },
 *             {  1, 1, 2 } });
 *     }
 * 
 *     private int addend1;
 *     private int addend2;
 *     private int sum;
 * 
 *     public IntAdderTest(int addend1, int addend2, int sum) {
 *         this.addend1 = addend1;
 *         this.addend2 = addend2;
 *         this.sum = sum;
 *     }
 * 
 *     &#064;Test
 *     public void test() {
 *         assertEquals(sum, IntAdder.add(addend1, addend2));
 *     }
 * }
 * </pre>
 * <p>
 * Each instance of <code>IntAdderTest</code> will be constructed using its
 * three-argument constructor and the data values provided by the
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
 * names like <code>[{index} {0} + {1} = {2}]</code>. If you don't use the name
 * parameter, then the current parameter index is used as the name.
 * <p>
 * You can also write:
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class IntAdderTest {
 *     &#064;Parameters(name = &quot;{index}: {0} + {1} = {2}&quot;)
 *     public static Iterable&lt;Object[]&gt; data() {
 *         return Arrays.asList(new Object[][] {
 *             { -1, 1, 0 },
 *             {  0, 0, 0 },
 *             {  1, 0, 1 },
 *             {  1, 1, 2 } });
 *     }
 *  
 *     &#064;Parameter(0)
 *     public int addend1;
 *
 *     &#064;Parameter(1)
 *     public int addend2;
 *
 *     &#064;Parameter(2)
 *     public int sum;
 *
 *     &#064;Test
 *     public void test() {
 *         assertEquals(sum, IntAdder.add(addend1, addend2));
 *     }
 * }
 * </pre>
 * <p>
 * Each instance of <code>IntAdderTest</code> will be constructed with the default constructor
 * and fields annotated by <code>&#064;Parameter</code>  will be initialized
 * with the data values in the <code>&#064;Parameters</code> method.
 *
 * <p>
 * The parameters can be provided as an array:
 * 
 * <pre>
 * &#064;Parameters(name = &quot;{index}: {0} + {1} = {2}&quot;)
 * public static Iterable&lt;Object[]&gt; data() {
 *     return Arrays.asList(new Object[][] {
 *         { -1, 1, 0 },
 *         {  0, 0, 0 },
 *         {  1, 0, 1 },
 *         {  1, 1, 2 } });
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

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public Parameterized(Class<?> klass) throws Throwable {
        super(klass, RunnersFactory.createRunnersForClass(klass));
    }

    private static class RunnersFactory {
        private static final ParametersRunnerFactory DEFAULT_FACTORY = new BlockJUnit4ClassRunnerWithParametersFactory();

        private final TestClass testClass;

        static List<Runner> createRunnersForClass(Class<?> klass)
                throws Throwable {
            return new RunnersFactory(klass).createRunners();
        }

        private RunnersFactory(Class<?> klass) {
            testClass = new TestClass(klass);
        }

        private List<Runner> createRunners() throws Throwable {
            Parameters parameters = getParametersMethod().getAnnotation(
                    Parameters.class);
            return Collections.unmodifiableList(createRunnersForParameters(
                    allParameters(), parameters.name(),
                    getParametersRunnerFactory()));
        }

        private ParametersRunnerFactory getParametersRunnerFactory()
                throws InstantiationException, IllegalAccessException {
            UseParametersRunnerFactory annotation = testClass
                    .getAnnotation(UseParametersRunnerFactory.class);
            if (annotation == null) {
                return DEFAULT_FACTORY;
            } else {
                Class<? extends ParametersRunnerFactory> factoryClass = annotation
                        .value();
                return factoryClass.newInstance();
            }
        }

        private TestWithParameters createTestWithNotNormalizedParameters(
                String pattern, int index, Object parametersOrSingleParameter) {
            Object[] parameters = (parametersOrSingleParameter instanceof Object[]) ? (Object[]) parametersOrSingleParameter
                    : new Object[] { parametersOrSingleParameter };
            return createTestWithParameters(testClass, pattern, index,
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
            List<FrameworkMethod> methods = testClass
                    .getAnnotatedMethods(Parameters.class);
            for (FrameworkMethod each : methods) {
                if (each.isStatic() && each.isPublic()) {
                    return each;
                }
            }

            throw new Exception("No public static parameters method on class "
                    + testClass.getName());
        }

        private List<Runner> createRunnersForParameters(
                Iterable<Object> allParameters, String namePattern,
                ParametersRunnerFactory runnerFactory) throws Exception {
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
            String className = testClass.getName();
            String methodName = getParametersMethod().getName();
            String message = MessageFormat.format(
                    "{0}.{1}() must return an Iterable of arrays.", className,
                    methodName);
            return new Exception(message);
        }

        private TestWithParameters createTestWithParameters(
                TestClass testClass, String pattern, int index,
                Object[] parameters) {
            String finalPattern = pattern.replaceAll("\\{index\\}",
                    Integer.toString(index));
            String name = MessageFormat.format(finalPattern, parameters);
            return new TestWithParameters("[" + name + "]", testClass,
                    Arrays.asList(parameters));
        }
    }
}
