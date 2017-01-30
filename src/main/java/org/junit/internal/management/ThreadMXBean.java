package org.junit.internal.management;

/**
 * Wrapper for {@link java.lang.management.ThreadMXBean}.
 */
public interface ThreadMXBean {
  /**
   * @see java.lang.management.ThreadMXBean#getThreadCpuTime(long)
   */
  long getThreadCpuTime(long id);

  /**
   * @see java.lang.management.ThreadMXBean#isThreadCpuTimeSupported()
   */
  boolean isThreadCpuTimeSupported();
}

