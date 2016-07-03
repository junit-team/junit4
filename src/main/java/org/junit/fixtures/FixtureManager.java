package org.junit.fixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.internal.Throwables;

/**
 * Manages the interaction of a {@link TestFixture}
 */
public class FixtureManager  {
    private final Stack<TearDown> tearDowns = new Stack<TearDown>();
    private final List<TestPostcondition> testPostconditions = new ArrayList<TestPostcondition>();
    private final Class<?> testClass;
    private final InstanceMethod testMethod;
    private State state = State.HEALTHY;

    public static FixtureManager forTestMethod(InstanceMethod testMethod) {
        return new FixtureManager(testMethod, testMethod.getMethod().getDeclaringClass());
    }

    public static FixtureManager forTestClass(Class<?> testClass) {
        return new FixtureManager(null, testClass);
    }

    /**
     * Initializes a fixture, registering any tear downs or postconditions registered by
     * the fixture initialization. If {@link TestFixture#initialize(FixtureContext)} throws
     * an exception, then all registered tear downs will be executed.
     *
     * @throws Exception exception thrown during {@code TestFixture#initialize(FixtureContext)}
     */
    public final void initializeFixture(TestFixture testFixture) throws Exception {
        state.checkCanModifyFixture();
        FixtureContextFacade context = new FixtureContextFacade();
        try {
            context.initialize(testFixture);
        } catch (Throwable e) {
            List<Throwable> suppressedErrors = new ArrayList<Throwable>();
            runAllTearDowns(suppressedErrors);
            
            for (Throwable suppressedError : suppressedErrors) {
                handleSuppressedError(e, suppressedError);
            }
            Throwables.rethrowAsException(e);
        }
    }
 
    /**
     * Resets this fixture to its initial state.
     */
    public final void reset() {
        state = State.HEALTHY;
        tearDowns.clear();
        testPostconditions.clear();
    }

    /**
     * Adds a {@link TearDown} to run after the test completes.
     */
    public final void addTearDown(TearDown tearDown) {
        if (tearDown == null) {
            throw new NullPointerException();
        }
        state.checkCanModifyFixture();
        tearDowns.add(tearDown);
    } 
 
    /**
     * Handle a suppressed exceptions that occurred while running  tear downs.
     * Subclasses can override this to do additional work (for example, in JDK 7
     * or higher, you could register the exceptions as suppressed exceptions).
     *
     * @param originalException exception that triggered the tear downs
     * @param suppressedErrors suppressed errors encountered during tear-down.
     */
    protected void handleSuppressedError(Throwable originalException, Throwable suppressedError) {
    }

    /**
     * Runs all of the {@link TearDown} instances managed by this class.
     * 
     * @param errors filled in with a list of all errors encountered while running the tear downs.
     */
    public final void runAllTearDowns(List<Throwable> errors) {
        state = state.startRunTearDowns();

        while (!tearDowns.isEmpty()) {
            try {
                tearDowns.pop().tearDown();
            } catch (Throwable e) {
                errors.add(e);
            }
        }
        state = State.COMPLETED;
    }
 
    /**
     * Removes and runs all of the {@link TearDown} instances managed by this class.
     * 
     * @param errors filled in with a list of all errors encountered while running the tear downs.
     * @throws Exception exception thrown from first failing postcondition
     */
    public final void runAllPostconditions() throws Exception {
        state = state.startRunPostConditions();

        for (TestPostcondition postcondition : testPostconditions) {
            postcondition.verify();
        }
    }

    /**
     * Constructs an instance.
     *
     * @param testMethod test method, or {@code null} if this is a class fixture.
     */
    protected FixtureManager(InstanceMethod testMethod, Class<?> testClass) {
        this.testMethod = testMethod;
        this.testClass = testClass;
    }

    private class FixtureContextFacade extends FixtureContext {
        private boolean initialized = false;
        
        public void initialize(TestFixture testFixture) throws Throwable {
            if (initialized) {
                throw new IllegalStateException("Already initialized");
            }
            try {
                testFixture.initialize(this);
            } finally {
                initialized = true;
            }
        }

        @Override
        public void addTearDown(TearDown tearDown) {
            checkUnitialized();
            tearDowns.add(tearDown);
        }

        @Override
        public void addTestPostcondition(TestPostcondition postcondition) {
            checkUnitialized();
            testPostconditions.add(postcondition);
        }
        
        @Override
        public InstanceMethod getInstanceMethod() {
            return testMethod;
        }
        
        @Override
        public Class<?> getTestClass() {
            return testClass;
        }

        private void checkUnitialized() {
            if (!initialized) {
                throw new IllegalStateException(
                        "Cannot modify context after initialize()");
            }
        }
    }
     
    private enum State {
        HEALTHY {
            @Override
            void checkCanModifyFixture() {
            }

            @Override
            State startRunPostConditions() {
                return READY_FOR_TEARDOWN;
            }

            @Override
            State startRunTearDowns() {
                return RUNNING_TEAR_DOWNS;
            }
        }, READY_FOR_TEARDOWN {
            @Override
            void checkCanModifyFixture() {
                throw new IllegalStateException();
            }

            @Override
            State startRunPostConditions() {
                throw new IllegalStateException();
            }

            @Override
            State startRunTearDowns() {
                return RUNNING_TEAR_DOWNS;
            }
        }, RUNNING_TEAR_DOWNS {
            @Override
            void checkCanModifyFixture() {
                throw new IllegalStateException();
            }

            @Override
            State startRunPostConditions() {
                throw new IllegalStateException();
            }

            @Override
            State startRunTearDowns() {
                throw new IllegalStateException();
            }
        }, COMPLETED {
            @Override
            void checkCanModifyFixture() {
                throw new IllegalStateException();
            }

            @Override
            State startRunPostConditions() {
               throw new IllegalStateException();
            }

            @Override
            State startRunTearDowns() {
                throw new IllegalStateException();
            }
        };

        abstract void checkCanModifyFixture();
        abstract State startRunPostConditions();
        abstract State startRunTearDowns();
    }
}
