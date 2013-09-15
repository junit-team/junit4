package org.junit.experimental.validator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates instances of Annotation Validators.
 */
public class AnnotationValidatorFactory {

    private static Map<ValidateWith, AnnotationValidator> fAnnotationTypeToValidatorMap =
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
            return new AnnotationValidator();
        }

        if (fAnnotationTypeToValidatorMap.containsKey(validateWithAnnotation)) {
            return fAnnotationTypeToValidatorMap.get(validateWithAnnotation);
        }

        Class<?> clazz = validateWithAnnotation.value();
        try {
            AnnotationValidator annotationValidator = (AnnotationValidator) clazz.newInstance();
            fAnnotationTypeToValidatorMap.put(validateWithAnnotation, annotationValidator);
            return annotationValidator;
        } catch (Exception e) {
            throw new RuntimeException("Error when creating AnnotationValidator class " + clazz.getName(), e);
        }
    }

}
