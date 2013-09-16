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
     * cached.
     *
     * @param validateWithAnnotation
     * @return An instance of the AnnotationValidator.
     */
    public AnnotationValidator createAnnotationValidator(ValidateWith validateWithAnnotation) {
        AnnotationValidator validator = fAnnotationTypeToValidatorMap.get(validateWithAnnotation);
        if (validator != null) {
            return validator;
        }

        Class<? extends AnnotationValidator> clazz = validateWithAnnotation.value();
        if (clazz == null) {
            throw new IllegalArgumentException("Can't create validator, value is null in annotation " + validateWithAnnotation.getClass().getName());
        }
        try {
            AnnotationValidator annotationValidator = clazz.newInstance();
            fAnnotationTypeToValidatorMap.putIfAbsent(validateWithAnnotation, annotationValidator);
            return fAnnotationTypeToValidatorMap.get(validateWithAnnotation);
        } catch (Exception e) {
            throw new RuntimeException("Exception received when creating AnnotationValidator class " + clazz.getName(), e);
        }
    }

}
