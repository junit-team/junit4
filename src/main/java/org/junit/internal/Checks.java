package org.junit.internal;

/** @since 4.13 */
public final class Checks {

    private Checks() {}

    /**
     * Checks that the given value is not {@code null}.
     *
     * @param value object reference to check
     * @return the passed-in value, if not {@code null}
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public static <T> T notNull(T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }

    /**
     * Checks that the given value is not {@code null}, using the given message
     * as the exception message if an exception is thrown.
     *
     * @param value object reference to check
     * @param message message to use if {@code value} is {@code null}
     * @return the passed-in value, if not {@code null}
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public static <T> T notNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }
}
