package org.junit.internal;

/**
 * Miscellaneous functions dealing with {@code Throwable}.
 *
 * @author kcooney@google.com (Kevin Cooney)
 * @since 4.12
 */
public final class Throwables {

    private Throwables() {
    }

    /**
     * Rethrows the given {@code Throwable}, allowing the caller to
     * declare that it throws {@code Exception}. This is useful when
     * your callers have nothing reasonable they can do when a
     * {@code Throwable} is thrown. This is declared to return {@code Exception}
     * so it can be used in a {@code throw} clause:
     * <pre>
     * try {
     *   doSomething();
     * } catch (Throwable e} {
     *   throw Throwables.rethrowAsException(e);
     * }
     * doSomethingLater();
     * </pre>
     *
     * @param e exception to rethrow
     * @return does not return anything
     * @since 4.12
     */
    public static Exception rethrowAsException(Throwable e) throws Exception {
        Throwables.<Exception>rethrow(e);
        return null; // we never get here
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void rethrow(Throwable e) throws T {
        throw (T) e;
    }
}
