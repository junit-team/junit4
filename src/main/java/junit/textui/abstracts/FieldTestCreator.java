package junit.textui.abstracts;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.model.*;

import java.lang.reflect.Field;
import java.util.List;

public class FieldTestCreator extends TestCreator {
    private final Object[] parameters;
    private final TestClass testClass;

    public FieldTestCreator(Object[] parameters, TestClass testClass) {
        this.parameters = parameters;
        this.testClass = testClass;
    }
    private List<FrameworkField> getAnnotatedFieldsByParameter() {
        return testClass.getAnnotatedFields(Parameter.class);
    }
    @Override
    public Object createTest() throws Exception {
        List<FrameworkField> annotatedFieldsByParameter = getAnnotatedFieldsByParameter();
        if (annotatedFieldsByParameter.size() != parameters.length) {
            throw new Exception(
                    "Wrong number of parameters and @Parameter fields."
                            + " @Parameter fields counted: "
                            + annotatedFieldsByParameter.size()
                            + ", available parameters: " + parameters.length
                            + ".");
        }
        Object testClassInstance = testClass.getJavaClass().newInstance();
        for (FrameworkField each : annotatedFieldsByParameter) {
            Field field = each.getField();
            Parameter annotation = field.getAnnotation(Parameter.class);
            int index = annotation.value();
            try {
                field.set(testClassInstance, parameters[index]);
            } catch (IllegalAccessException e) {
                IllegalAccessException wrappedException = new IllegalAccessException(
                        "Cannot set parameter '" + field.getName()
                                + "'. Ensure that the field '" + field.getName()
                                + "' is public.");
                wrappedException.initCause(e);
                throw wrappedException;
            } catch (IllegalArgumentException iare) {
                throw new Exception(testClass.getName()
                        + ": Trying to set " + field.getName()
                        + " with the value " + parameters[index]
                        + " that is not the right type ("
                        + parameters[index].getClass().getSimpleName()
                        + " instead of " + field.getType().getSimpleName()
                        + ").", iare);
            }
        }
        return testClassInstance;
    }
}