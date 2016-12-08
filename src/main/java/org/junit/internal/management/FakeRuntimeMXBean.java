package org.junit.internal.management;

import java.util.Collections;
import java.util.List;

/**
 * No-op implementation of RuntimeMXBean when the platform doesn't provide it.
 */
class FakeRuntimeMXBean implements RuntimeMXBean {

  /**
   * {@inheritDoc}
   *
   * <p>Always returns an empty list.
   */
  public List<String> getInputArguments() {
    return Collections.emptyList();
  }

}

