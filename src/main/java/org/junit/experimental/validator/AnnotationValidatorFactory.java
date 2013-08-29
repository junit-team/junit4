package org.junit.experimental.validator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationValidatorFactory {

    private static Map<ValidateWith, AnnotationValidator> fAnnotationTypeToValidatorMap =
            new ConcurrentHashMap<ValidateWith, AnnotationValidator>();

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
