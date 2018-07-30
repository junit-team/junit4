package org.junit.runner.manipulation;

/**
 * Thrown when an ordering does something invalid (like remove or add children)
 *
 * @since 4.13
 */
public class InvalidOrderingException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidOrderingException() {
    }

    public InvalidOrderingException(String message) {
        super(message);
    }

    public InvalidOrderingException(String message, Throwable cause) {
        super(message, cause);
    }
}
