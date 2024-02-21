package org.junit.tests.description;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.Description;

public class SuiteDescriptionTest {
    Description childless = Description.createSuiteDescription("a");
    Description anotherChildless = Description.createSuiteDescription("a");
    Description namedB = Description.createSuiteDescription("b");

    Description twoKids = descriptionWithTwoKids("foo", "bar");
    Description anotherTwoKids = descriptionWithTwoKids("foo", "baz");

    @Test
    public void equalsIsCorrect() {
        assertEquals(childless, anotherChildless);
        assertFalse(childless.equals(namedB));
        assertEquals(childless, twoKids);
        assertEquals(twoKids, anotherTwoKids);
        assertFalse(twoKids.equals(Integer.valueOf(5)));
    }

    @Test
    public void hashCodeIsReasonable() {
        assertEquals(childless.hashCode(), anotherChildless.hashCode());
        assertFalse(childless.hashCode() == namedB.hashCode());
    }

    private Description descriptionWithTwoKids(String first, String second) {
        Description twoKids = Description.createSuiteDescription("a");
        twoKids.addChild(Description.createTestDescription(getClass(), first));
        twoKids.addChild(Description.createTestDescription(getClass(), second));
        return twoKids;
    }
}
