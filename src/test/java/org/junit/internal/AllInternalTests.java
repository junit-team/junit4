package org.junit.internal;

import org.junit.internal.builders.AnnotatedBuilderTest;
import org.junit.internal.matchers.StacktracePrintingMatcherTest;
import org.junit.internal.matchers.ThrowableCauseMatcherTest;
import org.junit.internal.runners.ErrorReportingRunnerTest;
import org.junit.internal.runners.statements.ExpectExceptionTest;
import org.junit.internal.runners.statements.FailOnTimeoutTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AnnotatedBuilderTest.class,
        ChecksTest.class,
        ErrorReportingRunnerTest.class,
        ExpectExceptionTest.class,
        FailOnTimeoutTest.class,
        MethodSorterTest.class,
        StacktracePrintingMatcherTest.class,
        StackTracesTest.class,
        ThrowableCauseMatcherTest.class,
        ArrayComparisonFailureTest.class
})
public class AllInternalTests {
}
