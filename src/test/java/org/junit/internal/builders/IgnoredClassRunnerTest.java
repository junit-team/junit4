package org.junit.internal.builders;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.manipulation.NoTestsRemainException;

public class IgnoredClassRunnerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void exceptionThrownOnFilter() throws NoTestsRemainException {
        exception.expect(NoTestsRemainException.class);

        new IgnoredClassRunner(null).filter(null);
    }
}
