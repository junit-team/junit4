package org.junit.testsupport;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static org.junit.testsupport.EventCollectorMatchers.everyTestRunSuccessful;
import static org.junit.testsupport.EventCollectorMatchers.hasNoAssumptionFailure;
import static org.junit.testsupport.EventCollectorMatchers.hasSingleAssumptionFailure;

@RunWith(Parameterized.class)
public class EventCollectorMatchersTest {
    private static final Description DUMMY_DESCRIPTION = Description.EMPTY;

    private static final Failure DUMMY_FAILURE = new Failure(null, new RuntimeException("dummy message"));

    private static final Result DUMMY_RESULT = new Result();

    private static final EventCollector COLLECTOR_WITH_NO_EVENTS = new EventCollector();

    private static final EventCollector COLLECTOR_WITH_SINGLE_FAILURE = new EventCollector() {{
        testFailure(DUMMY_FAILURE);
    }};

    private static final EventCollector COLLECTOR_WITH_SINGLE_ASSUMPTION_FAILURE = new EventCollector() {{
        testAssumptionFailure(DUMMY_FAILURE);
    }};

    @Parameters(name = "{0}")
    public static Object[][] data() {
        return new Object[][] {
                {"everyTestRunSuccessful() matches if no failures are reported", COLLECTOR_WITH_NO_EVENTS, everyTestRunSuccessful()},
                {"everyTestRunSuccessful() does not match if failure is reported", COLLECTOR_WITH_SINGLE_FAILURE, not(everyTestRunSuccessful())},
                {"everyTestRunSuccessful() does not match if assumption failure is reported", COLLECTOR_WITH_SINGLE_ASSUMPTION_FAILURE, not(everyTestRunSuccessful())},
                {"hasNoAssumptionFailure() matches if no assumption failure is reported", COLLECTOR_WITH_NO_EVENTS, hasNoAssumptionFailure()},
                {"hasNoAssumptionFailure() does not match if assumption failure is reported", COLLECTOR_WITH_SINGLE_ASSUMPTION_FAILURE, not(hasNoAssumptionFailure())},
                {"hasSingleAssumptionFailure() matches if single assumption failure is reported", COLLECTOR_WITH_SINGLE_ASSUMPTION_FAILURE, hasSingleAssumptionFailure()},
                {"hasSingleAssumptionFailure() does not match if no assumption failure is reported", COLLECTOR_WITH_NO_EVENTS, not(hasSingleAssumptionFailure())}
        };
    }

    @Parameter(0)
    public String testName; //must be assigned. Otherwise the Parameterized runner fails.

    @Parameter(1)
    public EventCollector collector;

    @Parameter(2)
    public Matcher<EventCollector> matcher;

    @Test
    public void matchesCollector() {
        assertThat(collector, matcher);
    }
}
