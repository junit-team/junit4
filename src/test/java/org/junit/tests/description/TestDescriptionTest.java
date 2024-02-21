package org.junit.tests.description;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.Description;

public class TestDescriptionTest {
    @Test
    public void equalsIsFalseForNonTestDescription() {
        assertFalse(Description.createTestDescription(getClass(), "a").equals(Integer.valueOf(5)));
    }

    @Test
    public void equalsIsTrueForSameNameAndNoExplicitUniqueId() {
        assertTrue(Description.createSuiteDescription("Hello").equals(Description.createSuiteDescription("Hello")));
    }

    @Test
    public void equalsIsFalseForSameNameAndDifferentUniqueId() {
        assertFalse(Description.createSuiteDescription("Hello", 2).equals(Description.createSuiteDescription("Hello", 3)));
    }
}