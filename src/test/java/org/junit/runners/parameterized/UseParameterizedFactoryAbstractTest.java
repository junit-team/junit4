package org.junit.runners.parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

@RunWith(Parameterized.class)
@UseParametersRunnerFactory(UseParameterizedFactoryAbstractTest.MyParametersRunnerFactory.class)
public abstract class UseParameterizedFactoryAbstractTest {
    protected static boolean testFlag = false;

    @Parameters
    public static Collection<Object[]> createParameters() {
        List<Object[]> result = new ArrayList<Object[]>();
        result.add(new Object[] { "parameter1" });
        return result;
    }

    public static class MyParametersRunnerFactory implements
            ParametersRunnerFactory {

        public Runner createRunnerForTestWithParameters(TestWithParameters test)
                throws InitializationError {
            return new BlockJUnit4ClassRunnerWithParameters(test) {
                @Override
                protected void runChild(final FrameworkMethod method,
                        RunNotifier notifier) {
                    testFlag = true;
                    super.runChild(method, notifier);
                }
            };
        }
    }

}
