package org.junit.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureMatching;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.concurrent.Callable;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.function.ThrowingRunnable;
import org.junit.internal.AssumptionViolatedException;

public class VerifierRuleTest {
    public static class UsesErrorCollector {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.addError(new Throwable("message"));
        }
    }

    @Test
    public void usedErrorCollectorShouldFail() {
        assertThat(testResult(UsesErrorCollector.class), hasFailureContaining("message"));
    }

    public static class PassesAssumptionViolatedExceptionToErrorCollector {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.addError(new AssumptionViolatedException("message"));
        }
    }

    @Test
    public void passingAssumptionViolatedExceptionShouldResultInFailure() {
        assertThat(testResult(PassesAssumptionViolatedExceptionToErrorCollector.class), hasSingleFailureMatching(
                CoreMatchers.<Throwable>instanceOf(AssertionError.class)));
    }

    public static class UsesErrorCollectorTwice {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.addError(new Throwable("first thing went wrong"));
            collector.addError(new Throwable("second thing went wrong"));
        }
    }

    @Test
    public void usedErrorCollectorTwiceShouldFail() {
        PrintableResult testResult = testResult(UsesErrorCollectorTwice.class);
        assertThat(testResult, hasFailureContaining("first thing went wrong"));
        assertThat(testResult, hasFailureContaining("second thing went wrong"));
    }

    public static class UsesErrorCollectorCheckThat {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThat(3, is(4));
            collector.checkThat(5, is(6));
            collector.checkThat("reason 1", 7, is(8));
            collector.checkThat("reason 2", 9, is(16));
        }
    }

    @Test
    public void usedErrorCollectorCheckThatShouldFail() {
        PrintableResult testResult = testResult(UsesErrorCollectorCheckThat.class);
        assertThat(testResult, hasFailureContaining("was <3>"));
        assertThat(testResult, hasFailureContaining("was <5>"));
        assertThat(testResult, hasFailureContaining("reason 1"));
        assertThat(testResult, hasFailureContaining("was <7>"));
        assertThat(testResult, hasFailureContaining("reason 2"));
        assertThat(testResult, hasFailureContaining("was <9>"));
    }

    public static class UsesErrorCollectorCheckSucceeds {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkSucceeds(new Callable<Object>() {
                public Object call() throws Exception {
                    throw new RuntimeException("first!");
                }
            });
            collector.checkSucceeds(new Callable<Integer>() {
                public Integer call() throws Exception {
                    throw new RuntimeException("second!");
                }
            });
            Integer result = collector.checkSucceeds(new Callable<Integer>() {
                public Integer call() throws Exception {
                    return 1;
                }
            });
            assertEquals(Integer.valueOf(1), result);
        }
    }

    @Test
    public void usedErrorCollectorCheckSucceedsShouldFail() {
        PrintableResult testResult = testResult(UsesErrorCollectorCheckSucceeds.class);
        assertThat(testResult, hasFailureContaining("first!"));
        assertThat(testResult, hasFailureContaining("second!"));
    }

    public static class UsesErrorCollectorCheckSucceedsWithAssumptionViolatedException {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkSucceeds(new Callable<Object>() {
                public Object call() throws Exception {
                    throw new AssumptionViolatedException("message");
                }
            });
        }
    }

    @Test
    public void usedErrorCollectorCheckSucceedsWithAssumptionViolatedExceptionShouldFail() {
        PrintableResult testResult = testResult(UsesErrorCollectorCheckSucceedsWithAssumptionViolatedException.class);
        assertThat(testResult, hasSingleFailureMatching(CoreMatchers.<Throwable>instanceOf(AssertionError.class)));
        assertThat(testResult, hasFailureContaining("Callable threw AssumptionViolatedException"));
    }

    public static class UsesErrorCollectorCheckSucceedsPasses {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            assertEquals(3, collector.checkSucceeds(new Callable<Object>() {
                public Object call() throws Exception {
                    return 3;
                }
            }));
        }
    }

    @Test
    public void usedErrorCollectorCheckSucceedsShouldPass() {
        PrintableResult testResult = testResult(UsesErrorCollectorCheckSucceedsPasses.class);
        assertThat(testResult, isSuccessful());
    }

    public static class UsesErrorCollectorCheckThrowsMatchingClass {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThrows(IllegalArgumentException.class, new ThrowingRunnable() {
                public void run() throws Throwable {
                    throw new IllegalArgumentException();
                }
            });
        }
    }

    @Test
    public void usedErrorCollectorCheckThrowsMatchingClassShouldPass() {
        PrintableResult testResult = testResult(UsesErrorCollectorCheckThrowsMatchingClass.class);
        assertThat(testResult, isSuccessful());
    }

    public static class UsesErrorCollectorCheckThrowsClassMismatch {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThrows(IllegalArgumentException.class, new ThrowingRunnable() {
                public void run() throws Throwable {
                    throw new NullPointerException();
                }
            });
        }
    }

    @Test
    public void usedErrorCollectorCheckThrowsClassMismatchShouldFail() {
        PrintableResult testResult = testResult(UsesErrorCollectorCheckThrowsClassMismatch.class);
        assertThat(testResult, hasFailureContaining(
            "expected:<java.lang.IllegalArgumentException> but was:<java.lang.NullPointerException>"));
    }

    public static class UsesErrorCollectorCheckThrowsNothingThrown {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThrows(IllegalArgumentException.class, new ThrowingRunnable() {
                public void run() throws Throwable {
                }
            });
        }
    }

    @Test
    public void usedErrorCollectorCheckThrowsNothingThrownShouldFail() {
        PrintableResult testResult = testResult(UsesErrorCollectorCheckThrowsNothingThrown.class);
        assertThat(testResult, hasFailureContaining("but nothing was thrown"));
    }

    private static String sequence;

    public static class UsesVerifier {
        @Rule
        public Verifier collector = new Verifier() {
            @Override
            protected void verify() {
                sequence += "verify ";
            }
        };

        @Test
        public void example() {
            sequence += "test ";
        }
    }

    @Test
    public void verifierRunsAfterTest() {
        sequence = "";
        assertThat(testResult(UsesVerifier.class), isSuccessful());
        assertEquals("test verify ", sequence);
    }
}
