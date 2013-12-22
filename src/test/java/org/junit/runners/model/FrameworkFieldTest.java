package org.junit.runners.model;

import static org.junit.rules.ExpectedException.none;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FrameworkFieldTest {
    @Rule
    public final ExpectedException thrown = none();

    @Test
    public void cannotBeCreatedWithoutUnderlyingField() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("FrameworkField cannot be created without an underlying field.");
        new FrameworkField(null);
    }
}
