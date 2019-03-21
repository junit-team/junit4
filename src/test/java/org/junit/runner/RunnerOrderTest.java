package org.junit.runner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class RunnerOrderTest {

  @Test
  public void runnerWithFailingTests() throws InitializationError {
    ReportingRunner runner = new ReportingRunner(FailingTests.class);
    runner.run(new RunNotifier());

    assertFalse(runner.started);
    assertTrue(runner.failed);
    assertFalse(runner.finished);
  }

  public static class FailingTests {
    @Test
    public void this_test_is_supposed_to_fail() {
      fail();
    }
  }

  class ReportingRunner extends BlockJUnit4ClassRunner {

    private boolean started;
    private boolean failed;
    private boolean finished;

    public ReportingRunner(Class<?> testClass) throws InitializationError {
      super(testClass);
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
      throw new IllegalArgumentException();
    }

    @Override
    public void run(RunNotifier notifier) {
      RunListener listener = new RunListener() {
        @Override
        public void testStarted(Description description) throws Exception {
          started = true;
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
          failed = true;
        }

        @Override
        public void testFinished(Description description) throws Exception {
          finished = true;
        }
      };
      notifier.addListener(listener);
      super.run(notifier);
    }
  }

}
