package org.junit.experimental.theories;

import java.util.List;

/**
 * @since 4.4
 */
public abstract class ParameterSupplier {
	public abstract List<PotentialAssignment> getValueSources(ParameterSignature sig);
}
