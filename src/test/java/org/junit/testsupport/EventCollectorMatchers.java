package org.junit.testsupport;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Collection;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class EventCollectorMatchers {
    public static Matcher<EventCollector> everyTestRunSuccessful() {
        return allOf(hasNoFailure(), hasNoAssumptionFailure());
    }

    public static Matcher<EventCollector> hasNoAssumptionFailure() {
        return hasNumberOfAssumptionFailures(0);
    }

    public static Matcher<EventCollector> hasSingleAssumptionFailure() {
        return hasNumberOfAssumptionFailures(1);
    }

    public static Matcher<EventCollector> hasSingleAssumptionFailureWithMessage(
            String message) {
        return hasSingleAssumptionFailureWithMessage(equalTo(message));
    }

    public static Matcher<EventCollector> hasSingleAssumptionFailureWithMessage(
            Matcher<String> messageMatcher) {
        return new SingleFailureMatcher(messageMatcher, "assumption failure") {
            @Override
            List<Failure> getFailures(EventCollector collector) {
                return collector.getAssumptionFailures();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfAssumptionFailures(
            int numberOfFailures) {
        return new CountMatcher(numberOfFailures, "assumption failures") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getAssumptionFailures();
            }
        };
    }

    public static Matcher<EventCollector> hasNoFailure() {
        return hasNumberOfFailures(0);
    }

    public static Matcher<EventCollector> hasSingleFailure() {
        return hasNumberOfFailures(1);
    }

    public static Matcher<EventCollector> hasSingleFailureWithMessage(
            String message) {
        return hasSingleFailureWithMessage(equalTo(message));
    }

    public static Matcher<EventCollector> hasSingleFailureWithMessage(
            final Matcher<String> messageMatcher) {
        return new SingleFailureMatcher(messageMatcher, "failure") {
            @Override
            List<Failure> getFailures(EventCollector collector) {
                return collector.getFailures();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfFailures(
            int numberOfFailures) {
        return new CountMatcher(numberOfFailures, "failures") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getFailures();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfTestRunsFinished(
            int numberOfTestRuns) {
        return new CountMatcher(numberOfTestRuns, "test runs finished") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getTestRunsFinished();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfTestRunsStarted(
            int numberOfTestRuns) {
        return new CountMatcher(numberOfTestRuns, "test runs started") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getTestRunsStarted();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfTestSuitesFinished(
            int numberOfTestSuites) {
        return new CountMatcher(numberOfTestSuites, "test suites finished") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getTestSuitesFinished();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfTestSuiteStarted(
            int numberOfTestSuites) {
        return new CountMatcher(numberOfTestSuites, "test suites started") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getTestSuitesStarted();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfTestsFinished(
            int numberOfTests) {
        return new CountMatcher(numberOfTests, "tests finished") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getTestsFinished();
            }
        };
    }

    public static Matcher<EventCollector> hasTestFinished(
            final Class<?> testClass) {
        return new TestMatcher(testClass, "finished") {
            @Override
            List<Description> getTests(EventCollector collector) {
                return collector.getTestsFinished();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfTestsIgnored(
            int numberOfTests) {
        return new CountMatcher(numberOfTests, "tests ignored") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getTestsIgnored();
            }
        };
    }

    public static Matcher<EventCollector> hasTestIgnored(
            final Class<?> testClass) {
        return new TestMatcher(testClass, "ignored") {
            @Override
            List<Description> getTests(EventCollector collector) {
                return collector.getTestsIgnored();
            }
        };
    }

    public static Matcher<EventCollector> hasNumberOfTestsStarted(
            int numberOfTests) {
        return new CountMatcher(numberOfTests, "tests started") {
            @Override
            Collection<?> getItems(EventCollector collector) {
                return collector.getTestsStarted();
            }
        };
    }

    public static Matcher<EventCollector> hasTestStarted(
            final Class<?> testClass) {
        return new TestMatcher(testClass, "started") {
            @Override
            List<Description> getTests(EventCollector collector) {
                return collector.getTestsStarted();
            }
        };
    }

    private abstract static class CountMatcher extends
            TypeSafeMatcher<EventCollector> {
        private final int count;

        private final String name;

        CountMatcher(int count, String name) {
            this.count = count;
            this.name = name;
        }

        abstract Collection<?> getItems(EventCollector collector);

        @Override
        public boolean matchesSafely(EventCollector collector) {
            return getItems(collector).size() == count;
        }

        public void describeTo(org.hamcrest.Description description) {
            appendMessage(description, count);
        }

        @Override
        protected void describeMismatchSafely(EventCollector collector,
                org.hamcrest.Description description) {
            appendMessage(description, getItems(collector).size());
        }

        private void appendMessage(org.hamcrest.Description description,
                int countForMessage) {
            description.appendText("has ");
            description.appendValue(countForMessage);
            description.appendText(" ");
            description.appendText(name);
        }
    }

    private abstract static class SingleFailureMatcher extends
            TypeSafeMatcher<EventCollector> {
        private final CountMatcher countMatcher;

        private final Matcher<String> messageMatcher;

        private final String name;

        SingleFailureMatcher(Matcher<String> messageMatcher, String name) {
            this.countMatcher = new CountMatcher(1, name) {
                @Override
                Collection<?> getItems(EventCollector collector) {
                    return getFailures(collector);
                }
            };
            this.messageMatcher = messageMatcher;
            this.name = name;
        }

        abstract List<Failure> getFailures(EventCollector collector);

        @Override
        public boolean matchesSafely(EventCollector collector) {
            return countMatcher.matches(collector)
                    && messageMatcher.matches(getFailures(collector).get(0)
                            .getMessage());
        }

        public void describeTo(org.hamcrest.Description description) {
            description.appendText("has single ");
            description.appendText(name);
            description.appendText(" with message ");
            messageMatcher.describeTo(description);
        }

        @Override
        protected void describeMismatchSafely(EventCollector collector,
                org.hamcrest.Description description) {
            description.appendText("was ");
            countMatcher.describeMismatch(collector, description);
            description.appendText(": ");
            boolean first = true;
            for (Failure f : getFailures(collector)) {
                if (!first) {
                    description.appendText(" ,");
                }
                description.appendText("'");
                description.appendText(f.getMessage());
                description.appendText("'");
                first = false;
            }
        }
    }

    private abstract static class TestMatcher extends
            TypeSafeMatcher<EventCollector> {
        private final Class<?> testClass;

        private final String name;

        TestMatcher(Class<?> testClass, String name) {
            this.testClass = testClass;
            this.name = name;
        }

        abstract List<Description> getTests(EventCollector collector);

        @Override
        public boolean matchesSafely(EventCollector collector) {
            for (Description description : getTests(collector)) {
                if (testClass.getName().equals(description.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        public void describeTo(org.hamcrest.Description description) {
            description.appendText("has test ");
            description.appendValue(testClass);
            description.appendText(" ");
            description.appendText(name);
        }

        @Override
        protected void describeMismatchSafely(EventCollector collector,
                org.hamcrest.Description description) {
            List<Description> tests = getTests(collector);
            if (tests.isEmpty()) {
                description.appendText("has no test");
            } else {
                description.appendText("has tests ");
                boolean first = true;
                for (Description test : getTests(collector)) {
                    if (!first) {
                        description.appendText(" ,");
                    }
                    description.appendValue(test.getClassName());
                    first = false;
                }
            }
            description.appendText(" ");
            description.appendText(name);
        }
    }
}
