package org.junit.tests.validation;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.tests.validation.classes.BeforeClassAndTestFailButClassIsIgnored;
import org.junit.tests.validation.classes.HasAfterClassButTestIsIgnored;
import org.junit.tests.validation.classes.HasBeforeClassButTestIsFiltered;
import org.junit.tests.validation.classes.HasBeforeClassButTestIsFiltered.FilteredTests;
import org.junit.tests.validation.classes.HasBeforeClassButTestIsIgnored;
import org.junit.tests.validation.classes.HasBrokenRuleButTestIsIgnored;
import org.junit.tests.validation.classes.HasUnfilteredTest;

/**
 * Tests verifying that class-level fixtures ({@link BeforeClass} and
 * {@link AfterClass}) and rules ({@link ClassRule}) are not executed when there
 * are no test methods to be run in a test class because they have been ignored.
 * 
 */
public class ClassLevelMethodsOnlyRunWhenNecessaryTest {
    public static final String OUR_FAILURE_MSG = "@BeforeClass and @AfterClass methods should should not be run when no tests are executed!";

    @Test
    public void beforeFailsButTestMethodIsIgnored() {
        runClassAndVerifyNoFailures(HasBeforeClassButTestIsIgnored.class,
                "BeforeClass should not have been executed because the test method is ignored!");
    }

    @Test
    public void wholeClassIsIgnored() {
        runClassAndVerifyNoFailures(
                BeforeClassAndTestFailButClassIsIgnored.class,
                "BeforeClass should not have been executed because the whole test class is ignored!");
    }

    @Test
    public void afterFailsButTestMethodIsIgnored() {
        runClassAndVerifyNoFailures(
                HasAfterClassButTestIsIgnored.class,
                "There should not be any failures because the only test method of the test class is ignored!");
    }

    @Test
    public void beforeFailsButTestIsFiltered() {
        Result result = new JUnitCore().run(Request.classes(
                HasBeforeClassButTestIsFiltered.class, HasUnfilteredTest.class)
                .filterWith(CategoryFilter.exclude(FilteredTests.class)));
        analyseResult(result, "der fail");
    }

    private void runClassAndVerifyNoFailures(Class<?> klass,
            String testFailureDescription) {
        Result result = JUnitCore.runClasses(klass);
        analyseResult(result, testFailureDescription);
    }

    private void analyseResult(Result result, String testFailureDescription) {
        List<Failure> failures = result.getFailures();
        if (failures.isEmpty() == false) {
            analyzeFailure(failures.get(0), testFailureDescription);
        }
    }

    private void analyzeFailure(Failure failure, String testFailureDescription) {
        String actualFailureMsg = failure.getMessage();
        if (OUR_FAILURE_MSG.equals(actualFailureMsg)) {
            fail(testFailureDescription);
        }
        fail("Unexpected failure : " + actualFailureMsg);
    }

    @Test
    public void brokenRuleButTestMethodIsIgnored() {
        runClassAndVerifyNoFailures(HasBrokenRuleButTestIsIgnored.class,
                "The rule should have been executed because the test method is ignored!");
    }

}
