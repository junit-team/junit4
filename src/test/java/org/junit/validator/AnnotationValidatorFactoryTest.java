package org.junit.validator;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AnnotationValidatorFactoryTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createAnnotationValidator() {
        ValidateWith validateWith = SampleTestWithValidator.class.getAnnotation(ValidateWith.class);
        AnnotationValidator annotationValidator = new AnnotationValidatorFactory().createAnnotationValidator(validateWith);
        assertThat(annotationValidator, is(instanceOf(Validator.class)));
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
