package org.junit.function;

/**
 * Represents a supplier of results that may throw a {@code Throwable}.
 *
 * <p>This interface facilitates the use of {@link org.junit.Assert#assertThrows(Class, ThrowingSupplier)}
 * from Java 8 and above. It allows method references to methods without arguments (that declare checked
 * exceptions) to be passed directly into {@code assertThrows} without wrapping. It is not meant to be
 * implemented directly.
 *
 * @since 4.13
 * @see org.junit.Assert#assertThrows(Class, ThrowingSupplier)
 * @see org.junit.Assert#assertThrows(String, Class, ThrowingSupplier)
 */
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
