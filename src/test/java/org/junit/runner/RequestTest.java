package org.junit.runner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class RequestTest {

    @Test
    public void shouldReportFailureWhenSuiteCannotBeInitialized() {
        RunNotifier notifier = new RunNotifier();
        final FailureRunListener runListener = new FailureRunListener();
        notifier.addListener(runListener);

        Request.classes(new FailingComputer(), FooTest.class, BarTest.class)
               .getRunner()
               .run(notifier);

        assertThat("There should be exactly one failure for the failed suite and not one for each test class",
                   runListener.getFailures().size(), equalTo(1));
    }

    private static class FailingComputer extends Computer {

        @Override
        public Runner getSuite(RunnerBuilder builder, Class<?>[] classes)
                throws InitializationError {
            throw new InitializationError(new IllegalArgumentException());
        }

    }

    private static class FailureRunListener extends RunListener {

        private List<Failure> failures = new ArrayList<Failure>();

        @Override
        public void testFailure(Failure failure) throws Exception {
            failures.add(failure);
        }

        public List<? extends Failure> getFailures() {
            return failures;
        }

    }

    private static class FooTest {
    }

    private static class BarTest {
    }

}
