package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.internal.runners.rules.RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR;
import static org.junit.internal.runners.rules.RuleMemberValidator.CLASS_RULE_VALIDATOR;
import static org.junit.internal.runners.rules.RuleMemberValidator.RULE_METHOD_VALIDATOR;
import static org.junit.internal.runners.rules.RuleMemberValidator.RULE_VALIDATOR;

import java.util.ArrayList;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class RuleMemberValidatorTest {
    private final List<Throwable> errors = new ArrayList<Throwable>();

    @Test
    public void rejectProtectedClassRule() {
        TestClass target = new TestClass(TestWithProtectedClassRule.class);
        CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'temporaryFolder' must be public.");
    }

    public static class TestWithProtectedClassRule {
        @ClassRule
        protected static TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void rejectNonStaticClassRule() {
        TestClass target = new TestClass(TestWithNonStaticClassRule.class);
        CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'temporaryFolder' must be static.");
    }

    public static class TestWithNonStaticClassRule {
        @ClassRule
        public TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void acceptStaticTestRuleThatIsAlsoClassRule() {
        TestClass target = new TestClass(TestWithStaticClassAndTestRule.class);
        CLASS_RULE_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class TestWithStaticClassAndTestRule {
        @ClassRule
        @Rule
        public static TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void rejectClassRuleInNonPublicClass() {
        TestClass target = new TestClass(NonPublicTestWithClassRule.class);
        CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'temporaryFolder' must be declared in a public class.");
    }

    static class NonPublicTestWithClassRule {
        @ClassRule
        public static TestRule temporaryFolder = new TemporaryFolder();
    }
    
    /**
     * If there is any property annotated with @ClassRule then it must implement
     * {@link TestRule}
     * 
     * <p>This case has been added with 
     * <a href="https://github.com/junit-team/junit/issues/1019">Issue #1019</a>
     */
    @Test
    public void rejectClassRuleThatIsImplemetationOfMethodRule() {
        TestClass target = new TestClass(TestWithClassRuleIsImplementationOfMethodRule.class);
        CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'classRule' must implement TestRule.");
    }
    
    public static class TestWithClassRuleIsImplementationOfMethodRule {
        @ClassRule
        public static MethodRule classRule = new MethodRule() {
            
            public Statement apply(Statement base, FrameworkMethod method, Object target) {
                return base;
            }
        };
    }

    /**
     * If there is any method annotated with @ClassRule then it must return an 
     * implementation of {@link TestRule}
     * 
     * <p>This case has been added with 
     * <a href="https://github.com/junit-team/junit/issues/1019">Issue #1019</a>
     */
    @Test
    public void rejectClassRuleThatReturnsImplementationOfMethodRule() {
        TestClass target = new TestClass(TestWithClassRuleMethodThatReturnsMethodRule.class);
        CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'methodRule' must return an implementation of TestRule.");
    }

    public static class TestWithClassRuleMethodThatReturnsMethodRule {
        @ClassRule
        public static MethodRule methodRule() {
            return new MethodRule() {
                
                public Statement apply(Statement base, FrameworkMethod method, Object target) {
                    return base;
                }
            };
        }
    }
    
    /**
     * If there is any property annotated with @ClassRule then it must implement
     * {@link TestRule}
     * 
     * <p>This case has been added with 
     * <a href="https://github.com/junit-team/junit/issues/1019">Issue #1019</a>
     */
    @Test
    public void rejectClassRuleIsAnArbitraryObject() throws Exception {
        TestClass target = new TestClass(TestWithClassRuleIsAnArbitraryObject.class);
        CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'arbitraryObject' must implement TestRule.");
    }

    public static class TestWithClassRuleIsAnArbitraryObject {
        @ClassRule
        public static Object arbitraryObject = 1;
    }
    
    /**
     * If there is any method annotated with @ClassRule then it must return an 
     * implementation of {@link TestRule}
     * 
     * <p>This case has been added with 
     * <a href="https://github.com/junit-team/junit/issues/1019">Issue #1019</a> 
     */
    @Test
    public void rejectClassRuleMethodReturnsAnArbitraryObject() throws Exception {
        TestClass target = new TestClass(TestWithClassRuleMethodReturnsAnArbitraryObject.class);
        CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'arbitraryObject' must return an implementation of TestRule.");
    }

    public static class TestWithClassRuleMethodReturnsAnArbitraryObject {
        @ClassRule
        public static Object arbitraryObject() {
            return 1;
        }
    }
    
    @Test
    public void acceptNonStaticTestRule() {
        TestClass target = new TestClass(TestWithNonStaticTestRule.class);
        RULE_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class TestWithNonStaticTestRule {
        @Rule
        public TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void rejectStaticTestRule() {
        TestClass target = new TestClass(TestWithStaticTestRule.class);
        RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'temporaryFolder' must not be static or it must be annotated with @ClassRule.");
    }

    public static class TestWithStaticTestRule {
        @Rule
        public static TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void rejectStaticMethodRule() {
        TestClass target = new TestClass(TestWithStaticMethodRule.class);
        RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'someMethodRule' must not be static.");
    }

    public static class TestWithStaticMethodRule {
        @Rule
        public static MethodRule someMethodRule = new SomeMethodRule();
    }
    
    @Test
    public void acceptMethodRule() throws Exception {
        TestClass target = new TestClass(TestWithMethodRule.class);
        RULE_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class TestWithMethodRule {
        @Rule
        public MethodRule temporaryFolder = new MethodRule() {
            public Statement apply(Statement base, FrameworkMethod method,
                    Object target) {
                return null;
            }
        };
    }

    @Test
    public void rejectArbitraryObjectWithRuleAnnotation() throws Exception {
        TestClass target = new TestClass(TestWithArbitraryObjectWithRuleAnnotation.class);
        RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'arbitraryObject' must implement MethodRule or TestRule.");
    }

    public static class TestWithArbitraryObjectWithRuleAnnotation {
        @Rule
        public Object arbitraryObject = 1;
    }

    @Test
    public void methodRejectProtectedClassRule() {
        TestClass target = new TestClass(MethodTestWithProtectedClassRule.class);
        CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'getTemporaryFolder' must be public.");
    }

    public static class MethodTestWithProtectedClassRule {
        @ClassRule
        protected static TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void methodRejectNonStaticClassRule() {
        TestClass target = new TestClass(MethodTestWithNonStaticClassRule.class);
        CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'getTemporaryFolder' must be static.");
    }

    public static class MethodTestWithNonStaticClassRule {
        @ClassRule
        public TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void acceptMethodStaticTestRuleThatIsAlsoClassRule() {
        TestClass target = new TestClass(MethodTestWithStaticClassAndTestRule.class);
        CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class MethodTestWithStaticClassAndTestRule {
        @ClassRule
        @Rule
        public static TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void acceptMethodNonStaticTestRule() {
        TestClass target = new TestClass(TestMethodWithNonStaticTestRule.class);
        RULE_METHOD_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class TestMethodWithNonStaticTestRule {
        @Rule
        public TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void rejectMethodStaticTestRule() {
        TestClass target = new TestClass(TestMethodWithStaticTestRule.class);
        RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'getTemporaryFolder' must not be static or it must be annotated with @ClassRule.");
    }

    public static class TestMethodWithStaticTestRule {
        @Rule
        public static TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void rejectMethodStaticMethodRule() {
        TestClass target = new TestClass(TestMethodWithStaticMethodRule.class);
        RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'getSomeMethodRule' must not be static.");
    }

    public static class TestMethodWithStaticMethodRule {
        @Rule
        public static MethodRule getSomeMethodRule() { return new SomeMethodRule(); }
    }

    @Test
    public void methodAcceptMethodRuleMethod() throws Exception {
        TestClass target = new TestClass(MethodTestWithMethodRule.class);
        RULE_METHOD_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class MethodTestWithMethodRule {
        @Rule
        public MethodRule getTemporaryFolder() {
            return new MethodRule() {
                public Statement apply(Statement base, FrameworkMethod method,
                        Object target) {
                    return null;
                }
            };
        }
    }

    @Test
    public void methodRejectArbitraryObjectWithRuleAnnotation() throws Exception {
        TestClass target = new TestClass(MethodTestWithArbitraryObjectWithRuleAnnotation.class);
        RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'getArbitraryObject' must return an implementation of MethodRule or TestRule.");
    }

    public static class MethodTestWithArbitraryObjectWithRuleAnnotation {
        @Rule
        public Object getArbitraryObject() {
            return 1;
        }
    }

    private void assertOneErrorWithMessage(String message) {
        assertNumberOfErrors(1);
        assertEquals("Wrong error message:", message, errors.get(0).getMessage());
    }

    private void assertNumberOfErrors(int numberOfErrors) {
        assertEquals("Wrong number of errors:", numberOfErrors, errors.size());
    }
    
    private static final class SomeMethodRule implements MethodRule {
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            return base;
        }
    }
}
