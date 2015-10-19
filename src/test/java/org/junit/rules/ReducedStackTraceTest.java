package org.junit.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ReducedStackTraceTest {

  public static class StandardThrowingTest {
    @Test
    public void throwsException() {
      fail("Simulate a failing test");
    }
  }

  public static class ReducedStackThrowingTest extends StandardThrowingTest {
    @Rule
    public ReducedStackTrace reducer = new ReducedStackTrace();
  }

  @Test
  public void whenRuleIsAppliedStackTraceShouldStartWithTestName() {
    JUnitCore core = new JUnitCore();
    Result result = core.run(ReducedStackThrowingTest.class);

    assertEquals("Should have failures", 1, result.getFailureCount());

    StackTraceElement frame = last(result.getFailures().get(0).getException().getStackTrace());
    assertEquals("Should start with test method name", "throwsException", frame.getMethodName());
  }

  @Test
  public void whenRuleIsNotAppliedStackTraceDoesNotStartWithTestName() {
    JUnitCore core = new JUnitCore();
    Result result = core.run(StandardThrowingTest.class);

    assertEquals("Should have failures", 1, result.getFailureCount());

    StackTraceElement frame = last(result.getFailures().get(0).getException().getStackTrace());
    assertNotEquals("Should not start with test method name", "throwsException", frame.getMethodName());
  }

  private StackTraceElement last(StackTraceElement[] stack) {
    return stack[stack.length - 1];
  }

}
