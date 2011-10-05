/**
 *
 */
package org.junit;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;

/**
 * @author jerome@coffeebreaks.org
 * @since 2/11/11 8:14 AM
 */
public interface RuntimeCondition {
  boolean isTrue(FrameworkMethod method);
  boolean isTrue(Description description);
}
