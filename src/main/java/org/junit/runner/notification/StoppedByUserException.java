package org.junit.runner.notification;

/**
 * Thrown when a user has requested that the test run stop. Writers of
 * test running GUIs should be prepared to catch a <code>StoppedByUserException</code>.
 *
 * @see org.junit.runner.notification.RunNotifier
 * @since 4.0
 */
public class StoppedByUserException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
