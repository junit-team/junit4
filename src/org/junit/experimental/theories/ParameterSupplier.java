package org.junit.experimental.theories;

import java.util.List;




public abstract class ParameterSupplier {
	public abstract List<PotentialParameterValue> getValueSources(Object test, ParameterSignature sig);
}
