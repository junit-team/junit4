package org.junit.tests.running.classes;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.TestClass;
import org.junit.tests.experimental.rules.RuleFieldValidatorTest.TestWithNonStaticClassRule;
import org.junit.tests.experimental.rules.RuleFieldValidatorTest.TestWithProtectedClassRule;
import org.junit.validator.AnnotationValidator;
import org.junit.validator.ValidateWith;

public class ParentRunnerTest {
    public static String log = "";

    public static class FruitTest {
        @Test
        public void apple() {
            log += "apple ";
        }

        @Test
        public void /* must hash-sort after "apple" */Banana() {
            log += "banana ";
        }
    }

    @Test
    public void useChildHarvester() throws InitializationError {
        log = "";
        ParentRunner<?> runner = new BlockJUnit4ClassRunner(FruitTest.class);
        runner.setScheduler(new RunnerScheduler() {
            public void schedule(Runnable childStatement) {
                log += "before ";
                childStatement.run();
                log += "after ";
            }

            public void finished() {
                log += "afterAll ";
            }
        });

        runner.run(new RunNotifier());
        assertEquals("before apple after before banana after afterAll ", log);
    }

    @Test
    public void testMultipleFilters() throws Exception {
        JUnitCore junitCore = new JUnitCore();
        Request request = Request.aClass(ExampleTest.class);
        Request requestFiltered = request.filterWith(new Exclude("test1"));
        Request requestFilteredFiltered = requestFiltered
                .filterWith(new Exclude("test2"));
        Result result = junitCore.run(requestFilteredFiltered);
        assertThat(result.getFailures(), isEmpty());
        assertEquals(1, result.getRunCount());
    }

    private Matcher<List<?>> isEmpty() {
        return new TypeSafeMatcher<List<?>>() {
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("is empty");
            }

            @Override
            public boolean matchesSafely(List<?> item) {
                return item.size() == 0;
            }
        };
    }

    private static class Exclude extends Filter {
        private String methodName;

        public Exclude(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public boolean shouldRun(Description description) {
            return !description.getMethodName().equals(methodName);
        }

        @Override
        public String describe() {
            return "filter method name: " + methodName;
        }
    }

    public static class ExampleTest {
        @Test
        public void test1() throws Exception {
        }

        @Test
        public void test2() throws Exception {
        }

        @Test
        public void test3() throws Exception {
        }
    }

    @Test
    public void failWithHelpfulMessageForProtectedClassRule() {
        assertClassHasFailureMessage(TestWithProtectedClassRule.class,
                "The @ClassRule 'temporaryFolder' must be public.");
    }

    @Test
    public void failWithHelpfulMessageForNonStaticClassRule() {
        assertClassHasFailureMessage(TestWithNonStaticClassRule.class,
                "The @ClassRule 'temporaryFolder' must be static.");
    }

    private void assertClassHasFailureMessage(Class<?> klass, String message) {
        JUnitCore junitCore = new JUnitCore();
        Request request = Request.aClass(klass);
        Result result = junitCore.run(request);
        List<String> messages = new ArrayList<String>();
        for (Failure failure : result.getFailures()) {
            messages.add(failure.getMessage());
        }
        assertThat(messages, hasItem(message));

    }

    public static class ExampleAnnotationValidator extends AnnotationValidator {
        private static final String ANNOTATED_METHOD_CALLED = "annotated method called";
        private static final String ANNOTATED_FIELD_CALLED = "annotated field called";
        private static final String ANNOTATED_CLASS_CALLED = "annotated class called";

        @Override
        public List<Exception> validateAnnotatedClass(TestClass testClass) {
            return asList(new Exception(ANNOTATED_CLASS_CALLED));
        }

        @Override
        public List<Exception> validateAnnotatedField(FrameworkField field) {
            return asList(new Exception(ANNOTATED_FIELD_CALLED));
        }

        @Override
        public List<Exception> validateAnnotatedMethod(FrameworkMethod method) {
            return asList(new Exception(ANNOTATED_METHOD_CALLED));
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @ValidateWith(ExampleAnnotationValidator.class)
    public @interface ExampleAnnotationWithValidator {
    }

    public static class AnnotationValidatorMethodTest {
        @ExampleAnnotationWithValidator
        @Test
        public void test() {
        }
    }

    public static class AnnotationValidatorFieldTest {
        @ExampleAnnotationWithValidator
        private String field;

        @Test
        public void test() {
        }
    }

    @ExampleAnnotationWithValidator
    public static class AnnotationValidatorClassTest {
        @Test
        public void test() {
        }
    }

    @Test
    public void validatorIsCalledForAClass() {
        assertClassHasFailureMessage(AnnotationValidatorClassTest.class,
                ExampleAnnotationValidator.ANNOTATED_CLASS_CALLED);
    }

    @Test
    public void validatorIsCalledForAMethod() {
        assertClassHasFailureMessage(AnnotationValidatorMethodTest.class,
                ExampleAnnotationValidator.ANNOTATED_METHOD_CALLED);
    }

    @Test
    public void validatorIsCalledForAField() {
        assertClassHasFailureMessage(AnnotationValidatorFieldTest.class,
                ExampleAnnotationValidator.ANNOTATED_FIELD_CALLED);
    }
}
