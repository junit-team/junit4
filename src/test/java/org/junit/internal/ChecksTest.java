package org.junit.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.junit.internal.Checks.notNull;
import org.junit.Test;

/** Tests for {@link Checks}. */
public class ChecksTest {

    @Test
    public void notNullShouldReturnNonNullValues() {
        Double value = Double.valueOf(3.14);

        Double result = notNull(value);

        assertSame(value, result);
    }

    @Test
    public void notNullShouldThrowOnNullValues() {
        try {
            notNull(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertNull("message should be null", e.getMessage());
        }
    }

    @Test
    public void notNullWithMessageShouldReturnNonNullValues() {
        Float value = Float.valueOf(3.14f);

        Float result = notNull(value, "woops");

        assertSame(value, result);
    }

    @Test
    public void notNullWithMessageShouldThrowOnNullValues() {
        try {
            notNull(null, "woops");
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("message does not match", "woops", e.getMessage());
        }
    }

    @Test
    public void notNullWithNullMessageShouldThrowOnNullValues() {
        try {
            notNull(null, null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertNull("message should be null", e.getMessage());
        }
    }
}
