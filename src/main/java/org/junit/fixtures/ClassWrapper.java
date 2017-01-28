package org.junit.fixtures;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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
        wrappedClass = checkNotNull(classToWrap);
    }

    public ClassWrapper(Class<?> classToWrap, List<Annotation> classAnnotations) {
        wrappedClass = checkNotNull(classToWrap);
        annotations = makeImmutable(checkNotNull(classAnnotations));
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
                    if (wrappedClass == null) {
                        annotations = emptyList();
                    } else {
                        annotations = makeImmutable(asList(wrappedClass.getAnnotations()));
                    }
                }
            } finally { 
                lock.unlock();
            }
        }
        return annotations;
    }

    private static <T> List<T> makeImmutable(List<T> list) {
        return unmodifiableList(new ArrayList<T>(list));
    }

    private static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }
}
