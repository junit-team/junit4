package org.junit.fixtures;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ClassWrapper {
    private final ReentrantLock lock = new ReentrantLock();
    private final Class<?> wrappedClass;
    private volatile List<Annotation> annotations;

    public ClassWrapper(Class<?> classToWrap) {
        wrappedClass = classToWrap;
    }

    /**
     * Returns the underlying Java class.
     */
    public Class<?> getJavaClass() {
        return wrappedClass;
    }

    /**
     * Gets the annotations on the underlying Java class.
     */
    public List<Annotation> getAnnotations() {
        if (annotations == null) {
            lock.lock();
            try {
                if (annotations == null) {
                    annotations = unmodifiableList(
                            new ArrayList<Annotation>(
                                    asList(wrappedClass.getAnnotations())));
                }
            } finally { 
                lock.unlock();
            }
        }
        return annotations;
    }
}
