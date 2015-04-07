package org.junit.runners.parameterized;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class InvokeParameterizedMethod extends Statement {
    private final FrameworkMethod testMethod;
    private final Object target;
    private final Object[] parameters;

    public InvokeParameterizedMethod(FrameworkMethod testMethod, Object[] parameters, Object target) {
        this.testMethod = testMethod;
        this.target = target;
        this.parameters = parameters;
    }

    @Override
    public void evaluate() throws Throwable {
        testMethod.invokeExplosively(target, parameters);
    }
}