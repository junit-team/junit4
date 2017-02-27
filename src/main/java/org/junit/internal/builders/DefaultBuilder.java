package org.junit.internal.builders;

import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

public class DefaultBuilder extends RunnerBuilder {
  private Class<? extends Runner> defaultRunnerClass;
  private RunnerBuilder suiteBuilder;

  public DefaultBuilder(Class<? extends Runner> defaultRunnerClass, RunnerBuilder suiteBuilder) {
    this.defaultRunnerClass = defaultRunnerClass;
    this.suiteBuilder = suiteBuilder;
  }

  public Runner runnerForClass(Class<?> testClass) throws Throwable {
        return buildRunner(defaultRunnerClass, testClass, suiteBuilder);
    }
}
