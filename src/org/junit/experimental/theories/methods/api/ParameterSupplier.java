package org.junit.experimental.theories.methods.api;

import java.util.List;



public abstract class ParameterSupplier {
	public abstract List<?> getValues(Object test, ParameterSignature sig);
}
