package org.junit.experimental.theories.internal;

import java.util.Arrays;
import java.util.List;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

public class BooleanSupplier extends ParameterSupplier {

    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
        return Arrays.asList(PotentialAssignment.forValue("true", true),
                             PotentialAssignment.forValue("false", false));
    }

}
