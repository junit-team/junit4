package org.junit.experimental.validator;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates instances of Annotation Validators.
 */
public class AnnotationValidatorFactory {

    private static ConcurrentHashMap<ValidateWith, AnnotationValidator> fAnnotationTypeToValidatorMap =
            new ConcurrentHashMap<ValidateWith, AnnotationValidator>();

    /**
     * Creates the AnnotationValidator specified by the value in
     * {@link org.junit.experimental.validator.ValidateWith}. Instances are
     * cached. A null parameter return a default no-args {@link AnnotationValidator}.
     *
     * @param validateWithAnnotation
     * @return An instance of the AnnotationValidator.
     */
    public AnnotationValidator createAnnotationValidator(ValidateWith validateWithAnnotation) {
        if (validateWithAnnotation == null) {
            return new AnnotationValidator() {
            };
        }

        AnnotationValidator validator = fAnnotationTypeToValidatorMap.get(validateWithAnnotation);
        if (validator != null) {
            return validator;
        }

        Class<? extends AnnotationValidator> clazz = validateWithAnnotation.value();
        try {
            AnnotationValidator annotationValidator = clazz.newInstance();
            fAnnotationTypeToValidatorMap.putIfAbsent(validateWithAnnotation, annotationValidator);
            return annotationValidator;
        } catch (Exception e) {
            throw new RuntimeException("Error when creating AnnotationValidator class " + clazz.getName(), e);
        }
    }

}
