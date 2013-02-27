package org.junit.filters;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IgnoreFilterTest {
    @Test
    public void shouldNotRunIgnoredTestClass() throws Exception {
        final IgnoreFilter ignoreFilter = new IgnoreFilter();

        final Description description = Description.createSuiteDescription(IgnoredTestClass.class);

        assertFalse(ignoreFilter.shouldRun(description));
    }

    @Test
    public void shouldRunNonIgnoredTestClass() {
        final IgnoreFilter ignoreFilter = new IgnoreFilter();

        final Description description = Description.createSuiteDescription(NonIgnoredTestClass.class);

        assertTrue(ignoreFilter.shouldRun(description));
    }

    @Ignore
    private static class IgnoredTestClass {}

    private static class NonIgnoredTestClass {}
}
