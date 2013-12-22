package org.junit.runners.model;

import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;

import java.lang.reflect.Field;

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

    @Test
    public void hasToStringWhichPrintsFieldName() throws Exception {
        Field field = ClassWithDummyField.class.getField("dummyField");
        FrameworkField frameworkField = new FrameworkField(field);
        assertTrue(frameworkField.toString().contains("dummyField"));
    }

    private static class ClassWithDummyField {
        @SuppressWarnings("unused")
        public final int dummyField = 0;
    }
}
