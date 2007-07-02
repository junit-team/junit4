package org.junit.experimental.theories.methods.api;

import java.util.ArrayList;
import java.util.List;


public class TestedOnSupplier extends ParameterSupplier {
	@Override public List<Object> getValues(Object test, ParameterSignature sig) {
		ArrayList<Object> list = new ArrayList<Object>();
		TestedOn testedOn = (TestedOn) sig.getSupplierAnnotation();
		int[] ints = testedOn.ints();
		for (int i : ints) {
			list.add(i);
		}
		return list;
	}
}
