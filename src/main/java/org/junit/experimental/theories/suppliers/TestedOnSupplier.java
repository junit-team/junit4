package org.junit.experimental.theories.suppliers;

import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

/**
 * @see org.junit.experimental.theories.suppliers.TestedOn
 * @see org.junit.experimental.theories.ParameterSupplier
 */
public class TestedOnSupplier extends ParameterSupplier {
    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
        List<PotentialAssignment> list = new ArrayList<PotentialAssignment>();
        TestedOn testedOn = sig.getAnnotation(TestedOn.class);
        int[] ints = testedOn.ints();
        for (final int i : ints) {
            list.add(PotentialAssignment.forValue("ints", i));
        }
        return list;
    }
}
