package org.junit.internal.runners.statements;

import org.junit.TestCase;
import org.junit.runners.model.ArgumentFactory;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class InvokeMethod extends Statement {
    private final FrameworkMethod testMethod;
    private final Object target;

    public InvokeMethod(FrameworkMethod testMethod, Object target) {
        this.testMethod = testMethod;
        this.target = target;
    }

    @Override
    public void evaluate() throws Throwable {
        TestCase testCase = testMethod.getContextAs(TestCase.class);
        if (testCase!=null) {
            // specialised by test case
            testMethod.invokeExplosively(target, ArgumentFactory.convert(testCase.value(), testMethod));
        } else {
            // no argument call
            testMethod.invokeExplosively(target);
        }
    }
}