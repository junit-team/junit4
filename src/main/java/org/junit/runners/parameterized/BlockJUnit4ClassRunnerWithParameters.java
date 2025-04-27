package org.junit.runners.parameterized;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.StaticParameter;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * A {@link BlockJUnit4ClassRunner} with parameters support. Parameters can be
 * injected via constructor or into annotated fields.
 */
public class BlockJUnit4ClassRunnerWithParameters extends
        BlockJUnit4ClassRunner {
    private enum InjectionType {
        CONSTRUCTOR, FIELD
    }

    private final Object[] parameters;

    private final String name;

    public BlockJUnit4ClassRunnerWithParameters(TestWithParameters test)
            throws InitializationError {
        super(test.getTestClass());
        parameters = test.getParameters().toArray(
                new Object[test.getParameters().size()]);
        name = test.getName();
    }

    @Override
    public Object createTest() throws Exception {
        InjectionType injectionType = getInjectionType();
        switch (injectionType) {
            case CONSTRUCTOR:
                return createTestUsingConstructorInjection();
            case FIELD:
                return createTestUsingFieldInjection();
            default:
                throw new IllegalStateException("The injection type "
                        + injectionType + " is not supported.");
        }
    }

    private Object createTestUsingConstructorInjection() throws Exception {
        return getTestClass().getOnlyConstructor().newInstance(parameters);
    }

    private Object createTestUsingFieldInjection() throws Exception {
        List<FrameworkField> annotatedFieldsByParameter = getAnnotatedFieldsByParameter();
        List<FrameworkField> annotatedFieldsByStaticParameter = getAnnotatedFieldsByStaticParameter();

        if (annotatedFieldsByParameter.size() + annotatedFieldsByStaticParameter.size() != parameters.length) {
            throw new Exception(
                    "Wrong number of parameters and @Parameter fields."
                            + " @Parameter fields counted: "
                            + annotatedFieldsByParameter.size()
                            + ", available parameters: " + parameters.length
                            + ".");
        }

        // Static parameters
        for (FrameworkField each : annotatedFieldsByStaticParameter) {
            Field field = each.getField();
            StaticParameter annotation = field.getAnnotation(StaticParameter.class);
            int index = annotation.value();
            try {
                field.set(null, parameters[index]);
            } catch (IllegalArgumentException iare) {
                throw new Exception(getTestClass().getName()
                        + ": Trying to set " + field.getName()
                        + " with the value " + parameters[index]
                        + " that is not the right type ("
                        + parameters[index].getClass().getSimpleName()
                        + " instead of " + field.getType().getSimpleName()
                        + ").", iare);
            }
        }

        // Non-static parameters
        Object testClassInstance = getTestClass().getJavaClass().newInstance();
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
                throw new Exception(getTestClass().getName()
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

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected String testName(FrameworkMethod method) {
        return method.getName() + getName();
    }

    @Override
    protected void validateConstructor(List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
        if (getInjectionType() != InjectionType.CONSTRUCTOR) {
            validateZeroArgConstructor(errors);
        }
    }

    @Override
    protected void validateFields(List<Throwable> errors) {
        super.validateFields(errors);
        if (getInjectionType() == InjectionType.FIELD) {
            List<FrameworkField> parametricFields = new ArrayList<FrameworkField>();
            parametricFields.addAll(getAnnotatedFieldsByParameter());
            parametricFields.addAll(getAnnotatedFieldsByStaticParameter());

            int[] usedIndices = new int[parametricFields.size()];
            for (FrameworkField each : parametricFields) {
                int index = -1;
                Field annotatedField = each.getField();
                if (Modifier.isStatic(annotatedField.getModifiers())) {
                    StaticParameter staticParam = annotatedField.getAnnotation(StaticParameter.class);
                    if (staticParam == null) {
                        errors.add(new Exception("Invalid @Parameter annotation on the static field: "
                                + annotatedField.getName() + ". It should be @StaticParameter."));
                    } else {
                        index = staticParam.value();
                    }

                } else {
                    Parameter param = annotatedField.getAnnotation(Parameter.class);
                    if (param == null) {
                        errors.add(new Exception("Invalid @StaticParameter annotation on the field: "
                                + annotatedField.getName() + ". It should be @Parameter."));
                    } else {
                        index = param.value();
                    }
                }
                if (index < 0 || index > parametricFields.size() - 1) {
                    errors.add(new Exception("Invalid @Parameter or @StaticParameter value: "
                            + index + ". parametric fields counted: "
                            + parametricFields.size()
                            + ". Please use an index between 0 and "
                            + (parametricFields.size() - 1) + "."));
                } else {
                    usedIndices[index]++;
                }
            }
            for (int index = 0; index < usedIndices.length; index++) {
                int numberOfUse = usedIndices[index];
                if (numberOfUse == 0) {
                    errors.add(new Exception("Parameter " + index
                            + " is never used."));
                } else if (numberOfUse > 1) {
                    errors.add(new Exception("Parameter " + index
                            + " is used more than once (" + numberOfUse + ")."));
                }
            }
        }
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        Statement statement = childrenInvoker(notifier);
        statement = withBeforeParams(statement);
        statement = withAfterParams(statement);
        return statement;
    }

    private Statement withBeforeParams(Statement statement) {
        List<FrameworkMethod> befores = getTestClass()
                .getAnnotatedMethods(Parameterized.BeforeParam.class);
        return befores.isEmpty() ? statement : new RunBeforeParams(statement, befores);
    }

    private class RunBeforeParams extends RunBefores {
        RunBeforeParams(Statement next, List<FrameworkMethod> befores) {
            super(next, befores, null);
        }

        @Override
        protected void invokeMethod(FrameworkMethod method) throws Throwable {
            int paramCount = method.getMethod().getParameterTypes().length;
            method.invokeExplosively(null, paramCount == 0 ? (Object[]) null : parameters);
        }
    }

    private Statement withAfterParams(Statement statement) {
        List<FrameworkMethod> afters = getTestClass()
                .getAnnotatedMethods(Parameterized.AfterParam.class);
        return afters.isEmpty() ? statement : new RunAfterParams(statement, afters);
    }

    private class RunAfterParams extends RunAfters {
        RunAfterParams(Statement next, List<FrameworkMethod> afters) {
            super(next, afters, null);
        }

        @Override
        protected void invokeMethod(FrameworkMethod method) throws Throwable {
            int paramCount = method.getMethod().getParameterTypes().length;
            method.invokeExplosively(null, paramCount == 0 ? (Object[]) null : parameters);
        }
    }

    @Override
    protected Annotation[] getRunnerAnnotations() {
        Annotation[] allAnnotations = super.getRunnerAnnotations();
        Annotation[] annotationsWithoutRunWith = new Annotation[allAnnotations.length - 1];
        int i = 0;
        for (Annotation annotation: allAnnotations) {
            if (!annotation.annotationType().equals(RunWith.class)) {
                annotationsWithoutRunWith[i] = annotation;
                ++i;
            }
        }
        return annotationsWithoutRunWith;
    }

    private List<FrameworkField> getAnnotatedFieldsByStaticParameter() {
        return getTestClass().getAnnotatedFields(StaticParameter.class);
    }

    private List<FrameworkField> getAnnotatedFieldsByParameter() {
        return getTestClass().getAnnotatedFields(Parameter.class);
    }

    private InjectionType getInjectionType() {
        if (fieldsAreAnnotated()) {
            return InjectionType.FIELD;
        } else {
            return InjectionType.CONSTRUCTOR;
        }
    }

    private boolean fieldsAreAnnotated() {
        return !getAnnotatedFieldsByParameter().isEmpty() || !getAnnotatedFieldsByStaticParameter().isEmpty();
    }
}
