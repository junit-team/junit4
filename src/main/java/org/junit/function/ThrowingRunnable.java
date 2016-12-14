package org.junit.function;

/**
 * This interface facilitates the use of {@code expectThrows} from Java 8. It allows method references
 * to void methods (that declare checked exceptions) to be passed directly into {@code expectThrows}.
 * without wrapping. It is not meant to be implemented directly.
 *
 * @since 4.13
 */
public interface ThrowingRunnable {
    void run() throws Throwable;
}
