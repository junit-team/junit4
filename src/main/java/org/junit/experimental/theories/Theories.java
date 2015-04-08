package org.junit.experimental.theories;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * The Theories runner allows to test a certain functionality against a subset of an infinite set of data points.
 * <p>
 * A Theory is a piece of functionality (a method) that is executed against several data inputs called data points.
 * To make a test method a theory you mark it with <b>&#064;Theory</b>. To create a data point you create a public
 * field in your test class and mark it with <b>&#064;DataPoint</b>. The Theories runner then executes your test
 * method as many times as the number of data points declared, providing a different data point as
 * the input argument on each invocation.
 * </p>
 * <p>
 * A Theory differs from standard test method in that it captures some aspect of the intended behavior in possibly
 * infinite numbers of scenarios which corresponds to the number of data points declared. Using assumptions and
 * assertions properly together with covering multiple scenarios with different data points can make your tests more
 * flexible and bring them closer to scientific theories (hence the name).
 * </p>
 * <p>
 * For example:
 * <pre>
 *
 * &#064;RunWith(<b>Theories.class</b>)
 * public class UserTest {
 *      <b>&#064;DataPoint</b>
 *      public static String GOOD_USERNAME = "optimus";
 *      <b>&#064;DataPoint</b>
 *      public static String USERNAME_WITH_SLASH = "optimus/prime";
 *
 *      <b>&#064;Theory</b>
 *      public void filenameIncludesUsername(String username) {
 *          assumeThat(username, not(containsString("/")));
 *          assertThat(new User(username).configFileName(), containsString(username));
 *      }
 * }
 * </pre>
 * This makes it clear that the user's filename should be included in the config file name,
 * only if it doesn't contain a slash. Another test or theory might define what happens when a username does contain
 * a slash. <code>UserTest</code> will attempt to run <code>filenameIncludesUsername</code> on every compatible data
 * point defined in the class. If any of the assumptions fail, the data point is silently ignored. If all of the
 * assumptions pass, but an assertion fails, the test fails.
 * <p>
 * Defining general statements as theories allows data point reuse across a bunch of functionality tests and also
 * allows automated tools to search for new, unexpected data points that expose bugs.
 * </p>
 * <p>
 * The support for Theories has been absorbed from the Popper project, and more complete documentation can be found
 * from that projects archived documentation.
 * </p>
 *
 * @see <a href="http://web.archive.org/web/20071012143326/popper.tigris.org/tutorial.html">Archived Popper project documentation</a>
 * @see <a href="http://web.archive.org/web/20110608210825/http://shareandenjoy.saff.net/tdd-specifications.pdf">Paper on Theories</a>
 */
public class Theories extends BlockJUnit4ClassRunner {
    public Theories(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);
        validateDataPointFields(errors);
        validateDataPointMethods(errors);
    }

    private void validateDataPointFields(List<Throwable> errors) {
        Field[] fields = getTestClass().getJavaClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getAnnotation(DataPoint.class) == null && field.getAnnotation(DataPoints.class) == null) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                errors.add(new Error("DataPoint field " + field.getName() + " must be static"));
            }
            if (!Modifier.isPublic(field.getModifiers())) {
                errors.add(new Error("DataPoint field " + field.getName() + " must be public"));
            }
        }
    }

    private void validateDataPointMethods(List<Throwable> errors) {
        Method[] methods = getTestClass().getJavaClass().getDeclaredMethods();
        
        for (Method method : methods) {
            if (method.getAnnotation(DataPoint.class) == null && method.getAnnotation(DataPoints.class) == null) {
                continue;
            }
            if (!Modifier.isStatic(method.getModifiers())) {
                errors.add(new Error("DataPoint method " + method.getName() + " must be static"));
            }
            if (!Modifier.isPublic(method.getModifiers())) {
                errors.add(new Error("DataPoint method " + method.getName() + " must be public"));
            }
        }
    }

    @Override
    protected void validateConstructor(List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        for (FrameworkMethod each : computeTestMethods()) {
            if (each.getAnnotation(Theory.class) != null) {
                each.validatePublicVoid(false, errors);
                each.validateNoTypeParametersOnArgs(errors);
            } else {
                each.validatePublicVoidNoArg(false, errors);
            }
            
            for (ParameterSignature signature : ParameterSignature.signatures(each.getMethod())) {
                ParametersSuppliedBy annotation = signature.findDeepAnnotation(ParametersSuppliedBy.class);
                if (annotation != null) {
                    validateParameterSupplier(annotation.value(), errors);
                }
            }
        }
    }

    private void validateParameterSupplier(Class<? extends ParameterSupplier> supplierClass, List<Throwable> errors) {
        Constructor<?>[] constructors = supplierClass.getConstructors();
        
        if (constructors.length != 1) {
            errors.add(new Error("ParameterSupplier " + supplierClass.getName() + 
                                 " must have only one constructor (either empty or taking only a TestClass)"));
        } else {
            Class<?>[] paramTypes = constructors[0].getParameterTypes();
            if (!(paramTypes.length == 0) && !paramTypes[0].equals(TestClass.class)) {
                errors.add(new Error("ParameterSupplier " + supplierClass.getName() + 
                                     " constructor must take either nothing or a single TestClass instance"));
            }
        }
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> testMethods = new ArrayList<FrameworkMethod>(super.computeTestMethods());
        List<FrameworkMethod> theoryMethods = getTestClass().getAnnotatedMethods(Theory.class);
        testMethods.removeAll(theoryMethods);
        testMethods.addAll(theoryMethods);
        return testMethods;
    }

    @Override
    public Statement methodBlock(final FrameworkMethod method) {
        return new TheoryAnchor(method, getTestClass());
    }

    public static class TheoryAnchor extends Statement {
        private int successes = 0;

        private final FrameworkMethod testMethod;
        private final TestClass testClass;

        private List<AssumptionViolatedException> fInvalidParameters = new ArrayList<AssumptionViolatedException>();

        public TheoryAnchor(FrameworkMethod testMethod, TestClass testClass) {
            this.testMethod = testMethod;
            this.testClass = testClass;
        }

        private TestClass getTestClass() {
            return testClass;
        }

        @Override
        public void evaluate() throws Throwable {
            runWithAssignment(Assignments.allUnassigned(
                    testMethod.getMethod(), getTestClass()));
            
            //if this test method is not annotated with Theory, then no successes is a valid case
            boolean hasTheoryAnnotation = testMethod.getAnnotation(Theory.class) != null;
            if (successes == 0 && hasTheoryAnnotation) {
                Assert
                        .fail("Never found parameters that satisfied method assumptions.  Violated assumptions: "
                                + fInvalidParameters);
            }
        }

        protected void runWithAssignment(Assignments parameterAssignment)
                throws Throwable {
            if (!parameterAssignment.isComplete()) {
                runWithIncompleteAssignment(parameterAssignment);
            } else {
                runWithCompleteAssignment(parameterAssignment);
            }
        }

        protected void runWithIncompleteAssignment(Assignments incomplete)
                throws Throwable {
            for (PotentialAssignment source : incomplete
                    .potentialsForNextUnassigned()) {
                runWithAssignment(incomplete.assignNext(source));
            }
        }

        protected void runWithCompleteAssignment(final Assignments complete)
                throws Throwable {
            new BlockJUnit4ClassRunner(getTestClass().getJavaClass()) {
                @Override
                protected void collectInitializationErrors(
                        List<Throwable> errors) {
                    // do nothing
                }

                @Override
                public Statement methodBlock(FrameworkMethod method) {
                    final Statement statement = super.methodBlock(method);
                    return new Statement() {
                        @Override
                        public void evaluate() throws Throwable {
                            try {
                                statement.evaluate();
                                handleDataPointSuccess();
                            } catch (AssumptionViolatedException e) {
                                handleAssumptionViolation(e);
                            } catch (Throwable e) {
                                reportParameterizedError(e, complete
                                        .getArgumentStrings(nullsOk()));
                            }
                        }

                    };
                }

                @Override
                protected Statement methodInvoker(FrameworkMethod method, Object test) {
                    return methodCompletesWithParameters(method, complete, test);
                }

                @Override
                public Object createTest() throws Exception {
                    Object[] params = complete.getConstructorArguments();
                    
                    if (!nullsOk()) {
                        Assume.assumeNotNull(params);
                    }
                    
                    return getTestClass().getOnlyConstructor().newInstance(params);
                }
            }.methodBlock(testMethod).evaluate();
        }

        private Statement methodCompletesWithParameters(
                final FrameworkMethod method, final Assignments complete, final Object freshInstance) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    final Object[] values = complete.getMethodArguments();
                    
                    if (!nullsOk()) {
                        Assume.assumeNotNull(values);
                    }
                    
                    method.invokeExplosively(freshInstance, values);
                }
            };
        }

        protected void handleAssumptionViolation(AssumptionViolatedException e) {
            fInvalidParameters.add(e);
        }

        protected void reportParameterizedError(Throwable e, Object... params)
                throws Throwable {
            if (params.length == 0) {
                throw e;
            }
            throw new ParameterizedAssertionError(e, testMethod.getName(),
                    params);
        }

        private boolean nullsOk() {
            Theory annotation = testMethod.getMethod().getAnnotation(
                    Theory.class);
            if (annotation == null) {
                return false;
            }
            return annotation.nullsAccepted();
        }

        protected void handleDataPointSuccess() {
            successes++;
        }
    }
}
