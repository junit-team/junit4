package org.junit.validator;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates instances of Annotation Validators.
 *
 * @since 4.12
 */
public class AnnotationValidatorFactory {
    private static final ConcurrentHashMap<ValidateWith, AnnotationValidator> VALIDATORS_FOR_ANNOTATION_TYPES =
            new ConcurrentHashMap<ValidateWith, AnnotationValidator>();

    /**
     * Creates the AnnotationValidator specified by the value in
     * {@link org.junit.validator.ValidateWith}. Instances are
     * cached.
     *
     * @return An instance of the AnnotationValidator.
     *
     * @since 4.12
     */
    public AnnotationValidator createAnnotationValidator(ValidateWith validateWithAnnotation) {
        AnnotationValidator validator = VALIDATORS_FOR_ANNOTATION_TYPES.get(validateWithAnnotation);
        if (validator != null) {
            return validator;
        }

        Class<? extends AnnotationValidator> clazz = validateWithAnnotation.value();
        if (clazz == null) {
            throw new IllegalArgumentException("Can't create validator, value is null in annotation " + validateWithAnnotation.getClass().getName());
        }
        try {
            AnnotationValidator annotationValidator = clazz.newInstance();
            VALIDATORS_FOR_ANNOTATION_TYPES.putIfAbsent(validateWithAnnotation, annotationValidator);
            return VALIDATORS_FOR_ANNOTATION_TYPES.get(validateWithAnnotation);
        } catch (Exception e) {
            throw new RuntimeException("Exception received when creating AnnotationValidator class " + clazz.getName(), e);
        }
    }

}
