package junit.textui.abstracts;

import org.junit.runners.model.TestClass;

public class ConstructorTestCreator extends TestCreator {
    private final Object[] parameters;
    private final TestClass testClass;

    public ConstructorTestCreator(Object[] parameters, TestClass testClass) {
        this.parameters = parameters;
        this.testClass = testClass;
    }

    @Override
    public Object createTest() throws Exception {
        return testClass.getOnlyConstructor().newInstance(parameters);
    }
}