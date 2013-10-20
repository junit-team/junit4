package org.junit.tests.validation;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.Statement;
import org.junit.tests.validation.classes.BeforeClassAndTestFailButClassIsIgnored;
import org.junit.tests.validation.classes.HasAfterClassButTestIsIgnored;
import org.junit.tests.validation.classes.HasBeforeClassButNoTests;
import org.junit.tests.validation.classes.HasBeforeClassButTestIsFiltered;
import org.junit.tests.validation.classes.HasBeforeClassButTestIsIgnored;
import org.junit.tests.validation.classes.HasBrokenRuleButTestIsIgnored;
import org.junit.tests.validation.classes.HasUnfilteredTest;

/**
 * Tests verifying that methods annotated with {@link BeforeClass} and
 * {@link AfterClass} are not executed when there are no test methods to be run
 * in a test class because they have been ignored or filtered.
 * 
 * @author flpa
 * 
 */
public class ClassLevelMethodsOnlyRunWhenNecessaryTest {
    public static final String OUR_FAILURE_MSG= "@BeforeClass and @AfterClass methods should should not be run when no tests are executed!";

    public interface FilteredTests {
    }

    @Test
    public void beforeFailsButTestMethodIsIgnored() {
        runClassAndVerifyNoFailures(HasBeforeClassButTestIsIgnored.class,
                "BeforeClass should not have been executed because the test method is ignored!");
    }

    @Test
    public void wholeClassIsIgnored() {
        runClassAndVerifyNoFailures(
                BeforeClassAndTestFailButClassIsIgnored.class,
                "There should not be any failures because the only test method of the test class is ignored!");
    }

    @Test
    public void afterFailsButTestMethodIsIgnored() {
        runClassAndVerifyNoFailures(
                HasAfterClassButTestIsIgnored.class,
                "There should not be any failures because the only test method of the test class is ignored!");
    }

    @Test
    public void beforeFailsButTestIsFiltered() {
        Result result= new JUnitCore().run(Request.classes(
                HasBeforeClassButTestIsFiltered.class, HasUnfilteredTest.class)
                .filterWith(CategoryFilter.exclude(FilteredTests.class)));
        analyseResult(result, "der fail");
    }

    private void runClassAndVerifyNoFailures(Class<?> klass,
            String testFailureDescription) {
        Result result= JUnitCore.runClasses(klass);
        analyseResult(result, testFailureDescription);
    }

    private void analyseResult(Result result, String testFailureDescription) {
        List<Failure> failures= result.getFailures();
        if (failures.isEmpty() == false) {
            analyzeFailure(failures.get(0), testFailureDescription);
        }
    }

    private void analyzeFailure(Failure failure, String testFailureDescription) {
        String actualFailureMsg= failure.getMessage();
        if (OUR_FAILURE_MSG.equals(actualFailureMsg)) {
            fail(testFailureDescription);
        }
        fail("Unexpected failure : " + actualFailureMsg);
    }

    @Ignore("throws exception because no tests to execute")
    @Test
    public void beforeFailsButNoTests() {
        runClassAndVerifyNoFailures(
                HasBeforeClassButNoTests.class,
                "There should not be any failures because the only test method of the test class is ignored!");
    }

    public static class BrokenRule implements TestRule {

        public Statement apply(Statement base, Description description) {
            throw new RuntimeException("this rule is broken");
        }

    }

    @Test
    public void brokenRuleButTestMethodIsIgnored() {
        runClassAndVerifyNoFailures(HasBrokenRuleButTestIsIgnored.class,
                "The rule should have been executed because the test method is ignored!");
    }

}
