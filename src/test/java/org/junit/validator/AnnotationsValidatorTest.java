package org.junit.validator;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class AnnotationsValidatorTest {
    public static class ExampleAnnotationValidator extends AnnotationValidator {
        private static final String ANNOTATED_METHOD_CALLED= "annotated method called";

        private static final String ANNOTATED_FIELD_CALLED= "annotated field called";

        private static final String ANNOTATED_CLASS_CALLED= "annotated class called";

        @Override
        public List<Exception> validateAnnotatedClass(TestClass testClass) {
            return asList(new Exception(ANNOTATED_CLASS_CALLED));
        }

        @Override
        public List<Exception> validateAnnotatedField(FrameworkField field) {
            return asList(new Exception(ANNOTATED_FIELD_CALLED));
        }

        @Override
        public List<Exception> validateAnnotatedMethod(FrameworkMethod method) {
            return asList(new Exception(ANNOTATED_METHOD_CALLED));
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @ValidateWith(ExampleAnnotationValidator.class)
    public @interface ExampleAnnotationWithValidator {
    }

    public static class AnnotationValidatorMethodTest {
        @ExampleAnnotationWithValidator
        @Test
        public void test() {
        }
    }

    public static class AnnotationValidatorFieldTest {
        @ExampleAnnotationWithValidator
        private String field;

        @Test
        public void test() {
        }
    }

    @ExampleAnnotationWithValidator
    public static class AnnotationValidatorClassTest {
        @Test
        public void test() {
        }
    }

    @Test
    public void validatorIsCalledForAClass() {
        assertClassHasFailureMessage(AnnotationValidatorClassTest.class,
                ExampleAnnotationValidator.ANNOTATED_CLASS_CALLED);
    }

    @Test
    public void validatorIsCalledForAMethod() {
        assertClassHasFailureMessage(AnnotationValidatorMethodTest.class,
                ExampleAnnotationValidator.ANNOTATED_METHOD_CALLED);
    }

    @Test
    public void validatorIsCalledForAField() {
        assertClassHasFailureMessage(AnnotationValidatorFieldTest.class,
                ExampleAnnotationValidator.ANNOTATED_FIELD_CALLED);
    }

    private void assertClassHasFailureMessage(Class<?> klass,
            String expectedFailure) {
        AnnotationsValidator validator= new AnnotationsValidator();
        Collection<Exception> errors= validator
                .validateTestClass(new TestClass(klass));
        assertThat(errors.size(), is(1));
        assertThat(errors.iterator().next().getMessage(),
                is(expectedFailure));
    }
}
