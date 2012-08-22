package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.internal.runners.rules.RuleFieldValidator.CLASS_RULE_METHOD_VALIDATOR;
import static org.junit.internal.runners.rules.RuleFieldValidator.CLASS_RULE_VALIDATOR;
import static org.junit.internal.runners.rules.RuleFieldValidator.RULE_METHOD_VALIDATOR;
import static org.junit.internal.runners.rules.RuleFieldValidator.RULE_VALIDATOR;

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

@SuppressWarnings("deprecation")
public class RuleFieldValidatorTest {
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
        assertOneErrorWithMessage("The @Rule 'temporaryFolder' must not be static.");
    }

    public static class TestWithStaticTestRule {
        @Rule
        public static TestRule temporaryFolder = new TemporaryFolder();
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
        assertOneErrorWithMessage("The @Rule 'getTemporaryFolder' must not be static.");
    }

    public static class TestMethodWithStaticTestRule {
        @Rule
        public static TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
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
}
