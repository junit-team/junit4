package org.junit.internal.builders;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;

/**
 * <tt>ParameterRunnerBuilder</tt>s perform the work within a {@link org.junit.rules.ParameterRule}
 * by providing the runner that will execute the test class the containing rule is in, as well as any
 * other actions it wants to perform on the input values.
 *
 * @since 4.12
 */
public interface ParameterRunnerBuilder {

    Runner build(Class<?> type, String pattern, int index, Object[] parameters) throws InitializationError;

}
