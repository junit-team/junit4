package org.junit.runner.notification.spi;

import org.junit.runner.notification.RunListener;

/**
 * Test RunListener failing during initialization. Shouldn't break usual lifecycle.
 *
 * @see org.junit.runner.notification.RunNotifierLoadListenersUsingSpiTest
 * @since 4.12
 */
public class FailingRunListener extends RunListener {
    public FailingRunListener() throws Exception {
        throw new Exception("Initialization error");
    }
}
