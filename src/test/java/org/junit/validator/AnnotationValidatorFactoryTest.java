package org.junit.validator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.annotation.Annotation;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class AnnotationValidatorFactoryTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createAnnotationValidator() {
        ValidateWith validateWith = SampleTestWithValidator.class.getAnnotation(ValidateWith.class);
        AnnotationValidator annotationValidator = new AnnotationValidatorFactory().createAnnotationValidator(validateWith);
        assertThat(annotationValidator, is(instanceOf(Validator.class)));
    }

    @Test
    public void exceptionWhenAnnotationWithNullClassIsPassedIn() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Can't create validator, value is null in " +
                "annotation org.junit.validator.AnnotationValidatorFactoryTest$ValidatorWithNullValue");

        new AnnotationValidatorFactory().createAnnotationValidator(new ValidatorWithNullValue());
    }


    public static class ValidatorWithNullValue implements ValidateWith {
        public Class<? extends AnnotationValidator> value() {
            return null;
        }

        public Class<? extends Annotation> annotationType() {
            return ValidateWith.class;
        }
    }

    @ValidateWith(value = Validator.class)
    public static class SampleTestWithValidator {
    }

    public static class Validator extends AnnotationValidator {
    }

    @Test
    public void exceptionWhenAnnotationValidatorCantBeCreated() {
        ValidateWith validateWith = SampleTestWithValidatorThatThrowsException.class.getAnnotation(ValidateWith.class);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Exception received when creating AnnotationValidator class " +
                "org.junit.validator.AnnotationValidatorFactoryTest$ValidatorThatThrowsException");
        new AnnotationValidatorFactory().createAnnotationValidator(validateWith);
    }

    @ValidateWith(value = ValidatorThatThrowsException.class)
    public static class SampleTestWithValidatorThatThrowsException {
    }

    public static class ValidatorThatThrowsException extends AnnotationValidator {
        public ValidatorThatThrowsException() throws InstantiationException {
            throw new InstantiationException("Simulating exception in test");
        }
    }
}
