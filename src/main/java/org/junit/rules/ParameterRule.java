package org.junit.rules;


import org.junit.internal.builders.ParameterRunnerBuilder;

/**
 * A ParameterRule allows Paraterized tests to customize how they're
 * run and allows the developer to specify logic around their parameters
 * by either modifyting them in the rule, or by getting the rule to return
 * a builder that generates a custom runner.
 *
 * @see ParameterRunnerBuilder
 *
 * @since 4.12
 */
public interface ParameterRule {

   ParameterRunnerBuilder apply(ParameterRunnerBuilder builder);

}
