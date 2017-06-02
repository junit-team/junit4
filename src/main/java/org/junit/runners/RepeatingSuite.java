package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * A suite that allows running the specified classes a specified number of
 * times, and optionally allows a random shuffling of the test classes and
 * the test methods at each repetition.
 * <p>
 * Example usage:
 * 
 * <pre>
 * &#64;RunWith(RepeatingSuite.class)
 * &#64;RepeatingSuite.RepeatingSuiteClasses(repeat = 2,
 *         shuffleClasses = false, shuffleMethods = false,
 *         classes = { MyClass1.class, MyClass2.class, MyClass3.class })
 * public class MySuite {
 * }
 * </pre>
 * 
 * @author Laurent Cohen
 * @since 4.13
 */
public class RepeatingSuite extends Suite {
    /**
     * Called reflectively on classes annotated with
     * {@code @RunWith(Suite.class)}.
     * 
     * @param suiteClass
     *            the root class.
     * @throws InitializationError
     *             if any error occurs.
     */
    public RepeatingSuite(final Class<?> suiteClass)
            throws InitializationError {
        super(suiteClass, getRunners(suiteClass));
    }

    /**
     * Build the runners for the classes in the repeated suite. There is one
     * distinct runner for each classs for each iteration, resulting in
     * {@code nbRepeat * nbClasses} runners.
     * 
     * @param suiteClass
     *            the root class.
     * @return a list of runners.
     * @throws InitializationError
     *             if any error occurs.
     */
    private static List<Runner> getRunners(final Class<?> suiteClass)
            throws InitializationError {
        final RepeatingSuiteClasses annotation = 
                suiteClass.getAnnotation(RepeatingSuiteClasses.class);
        if (annotation == null) {
            throw new InitializationError(String.format(
                    "class '%s' must have a RepeatingSuiteClasses annotation",
                    suiteClass.getName()));
        }
        int repeat = annotation.repeat();
        if (repeat <= 0) {
            throw new InitializationError(String.format(
                    "class '%s' must have a repeat >= 1, currently %d",
                    suiteClass.getName(), repeat));
        }
        List<Class<?>> classes = Arrays.asList(annotation.classes());
        List<Runner> runners = new ArrayList<Runner>(repeat * classes.size());
        for (int i = 0; i < repeat; i++) {
            final String suffix = String.format("[%d]", i);
            List<Class<?>> tmp = new ArrayList<Class<?>>(classes);
            if (annotation.shuffleClasses() && !tmp.isEmpty()) {
                Collections.shuffle(tmp);
            }
            for (final Class<?> testClass : tmp) {
                runners.add(new BlockJUnit4ClassRunner(testClass) {
                    @Override
                    protected String getName() {
                        return super.getName() + suffix;
                    }

                    @Override
                    protected String testName(final FrameworkMethod method) {
                        return super.testName(method) + suffix;
                    }

                    @Override
                    protected List<FrameworkMethod> getChildren() {
                        List<FrameworkMethod> children = super.getChildren();
                        if (annotation.shuffleMethods()) {
                            Collections.shuffle(children);
                        }
                        return children;
                    }
                });
            }
        }
        return runners;
    }

    /**
     * The {@code RepeatedSuiteClasses} annotation specifies the classes to be
     * run, along with the number of repetitions and random shuffling of the
     * classes, when a class annotated with
     * {@code @RunWith(RepeatingSuite.class)} is run.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface RepeatingSuiteClasses {
        /**
         * @return the classes to be run.
         */
        public Class<?>[] classes();

        /**
         * @return whether to shuffle the classes at each repetition.
         */
        public boolean shuffleClasses() default false;

        /**
         * @return whether to shuffle the methods within each class at
         *         each repetition.
         */
        public boolean shuffleMethods() default false;

        /**
         * @return the number of times to repeat.
         */
        public int repeat() default 1;
    }
}
