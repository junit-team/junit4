package org.junit;

/**
 * A functional interface used to call code that is expected to throw an exception
 * @param <T> The type of exception expected to be thrown by {@link ExceptionSupplier#get()}
 */
public interface ExceptionSupplier<T extends Throwable> {
    /**
     * Execute code that is expected to throw an exception
     * @throws T the type of exception expected to be thrown
     */
    void get() throws T, Exception;
}
