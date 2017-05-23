package org.junit.rules;

public interface RetryDecider {
    void reportSuccess();

    boolean reportFailure(Throwable throwable);
}
