package org.junit.runners.model;

import java.lang.annotation.Annotation;

/**
 * A model element that may have annotations.
 * 
 * @since 4.12
 */
public interface Annotatable {
    /**
     * Returns the model elements' annotations.
     */
    Annotation[] getAnnotations();
}
