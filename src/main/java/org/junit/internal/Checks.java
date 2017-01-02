package org.junit.internal;

public final class Checks extends Classes {
    private Checks() {}

    public static <T> T notNull(T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }

    public static <T> T notNull(T value, String message, Object... args) {
        if (value == null) {
            throw new NullPointerException(String.format(message, args));
        }
        return value;
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }
 
    public static void checkArgument(boolean expression, String message,Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void notEmpty(String value) {
        checkArgument(value.length() > 0);
    }
 
    public static void notEmpty(String value, String message, Object... args) {
        checkArgument(value.length() > 0, message, args);
    }
}
