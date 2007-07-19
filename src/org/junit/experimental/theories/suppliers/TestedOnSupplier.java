package org.junit.experimental.theories.suppliers;

import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialParameterValue;



public class TestedOnSupplier extends ParameterSupplier {
	@Override public List<PotentialParameterValue> getValueSources(Object test, ParameterSignature sig) {
		List<PotentialParameterValue> list = new ArrayList<PotentialParameterValue>();
		TestedOn testedOn = (TestedOn) sig.getSupplierAnnotation();
		int[] ints = testedOn.ints();
		for (final int i : ints) {
			list.add(PotentialParameterValue.forValue(i));
		}
		return list;
	}
}
