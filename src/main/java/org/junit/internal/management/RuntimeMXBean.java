package org.junit.internal.management;

import java.util.List;

/**
 * Wrapper for {@link java.lang.management.RuntimeMXBean}.
 */
public interface RuntimeMXBean {

  /**
   * @see java.lang.management.RuntimeMXBean#getInputArguments()
   */
  List<String> getInputArguments();
}
