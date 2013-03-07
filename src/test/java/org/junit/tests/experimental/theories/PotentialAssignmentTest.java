package org.junit.tests.experimental.theories;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;

public class PotentialAssignmentTest {

    @Test
    public void shouldUseQuotedValueInDescription() throws CouldNotGenerateValueException {
        String name = "stringDatapoint";
        Object value = new Object() {
            @Override
            public String toString() {
                return "string value";
            }
        };

        PotentialAssignment assignment = PotentialAssignment.forValue(name, value);

        assertEquals("\"string value\" <from stringDatapoint>", assignment.getDescription());
    }

    @Test
    public void shouldNotUseQuotesForNullValueDescriptions() throws CouldNotGenerateValueException {
        String name = "nullDatapoint";
        Object value = null;

        PotentialAssignment assignment = PotentialAssignment.forValue(name, value);

        assertEquals("null <from nullDatapoint>", assignment.getDescription());
    }

    @Test
    public void shouldIncludeFailureInDescriptionIfToStringFails() throws CouldNotGenerateValueException {
        String name = "explodingValue";
        Object value = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Oh no!");
            }
        };

        PotentialAssignment assignment = PotentialAssignment.forValue(name, value);

        assertEquals("[toString() threw RuntimeException: Oh no!] <from explodingValue>", assignment.getDescription());
    }

    @Test
    public void shouldReturnGivenValue() throws CouldNotGenerateValueException {
        Object value = new Object();
        PotentialAssignment assignment = PotentialAssignment.forValue("name", value);
        assertEquals(value, assignment.getValue());
    }

}
