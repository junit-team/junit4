package org.junit.internal.management;

/**
 * No-op implementation of ThreadMXBean when the platform doesn't provide it.
 */
final class FakeThreadMXBean implements ThreadMXBean {

  /**
   * {@inheritDoc}
   *
   * <p>Always throws an {@link UnsupportedOperationException}
   */
  public long getThreadCpuTime(long id) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Always returns false.
   */
  public boolean isThreadCpuTimeSupported() {
    return false;
  }

}

