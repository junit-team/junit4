package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The {code ReducedStackTrace} rule allows you to remove stack frames leading to
 * your test method from junit's error logs.
 *
 * <h3>Usage</h3>
 * <pre> public class SimpleFailingTest {
 *     &#064;Rule
 *     public ReducedStackTrace reducer = new ReducedStackTrace();
 *
 *     &#064;Test
 *     public void throwsException() {
 *         throw new NullPointerException();
 *     }
 * }</pre>
 *
 * <p>
 * You have to add {@code ReducedStackTrace} rule to your test. The only noticable
 * change should be that stack traces on failing tests is now reduced so that errors
 * could be identified more quickly (esp. when working from the command line).
 */
public class ReducedStackTrace implements TestRule {
  public Statement apply(Statement base, Description description) {
    return new ReducedStackTraceStatement(base, description);
  }

  private class ReducedStackTraceStatement extends Statement {
    private static final int FRAME_NOT_FOUND = -1;

    private final Statement base;
    private final Description description;

    public ReducedStackTraceStatement(Statement base, Description description) {
      this.base = base;
      this.description = description;
    }

    @Override
    public void evaluate() throws Throwable {
      try {
        base.evaluate();
      } catch (Throwable e) {
        StackTraceElement[] stack = e.getStackTrace();
        int upperFrame = getUppermostTestFrame(stack) + 1; // Inclusive.

        if (upperFrame != FRAME_NOT_FOUND) {
          // Arrays.copyOfRange() is only available in Java 6 and onwards...
          StackTraceElement[] copyOfStack = new StackTraceElement[upperFrame];
          System.arraycopy(stack, 0, copyOfStack, 0, upperFrame);
          e.setStackTrace(copyOfStack);
        }

        throw e;
      }
    }

    /**
     * Get the highest stack frame containing the test method.
     */
    private int getUppermostTestFrame(StackTraceElement[] stack) {
      int frameIndex;

      // Traverse the stack in reverse order to catch the earliest call
      // to the test method.
      for (frameIndex = stack.length - 1; frameIndex >= 0; frameIndex--) {
        if (stack[frameIndex].getMethodName().equals(description.getMethodName())) {
          return frameIndex;
        }
      }

      return FRAME_NOT_FOUND;
    }
  }
}
