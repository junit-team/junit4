package org.junit.experimental.validator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class AnnotationValidatorFactoryTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void nullArgumentReturnsDefaultAnnotationValidator() {
        AnnotationValidator annotationValidator = new AnnotationValidatorFactory().createAnnotationValidator(null);
        assertThat(annotationValidator, is(instanceOf(AnnotationValidator.class)));
    }

    @Test
    public void exceptionWhenValidatorIsNotAnAnnotationValidator() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Error when creating AnnotationValidator class " +
                "org.junit.experimental.validator.AnnotationValidatorFactoryTest$ValidatorThatThrowsException");

        ValidateWith validateWith = SampleTestWithValidatorThatThrowsException.class.getAnnotation(ValidateWith.class);
        new AnnotationValidatorFactory().createAnnotationValidator(validateWith);
    }

    @Test
    public void createAnnotationValidator() {
        ValidateWith validateWith = SampleTestWithValidator.class.getAnnotation(ValidateWith.class);
        AnnotationValidator annotationValidator = new AnnotationValidatorFactory().createAnnotationValidator(validateWith);
        assertThat(annotationValidator, is(instanceOf(Validator.class)));
    }


    @ValidateWith(value = ValidatorThatThrowsException.class)
    public static class SampleTestWithValidatorThatThrowsException {
    }

    public static class ValidatorThatThrowsException extends AnnotationValidator {
        public ValidatorThatThrowsException() throws InstantiationException {
            throw new InstantiationException("Simulating exception in test");
        }
    }

    @ValidateWith(value = Validator.class)
    public static class SampleTestWithValidator {
    }


    public static class Validator extends AnnotationValidator {
    }
}
