package org.junit.fixtures;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class InstanceMethod {
    private final ReentrantLock lock = new ReentrantLock();
    private final ClassWrapper javaClass;
    private final Method method;
    private final Object object;
    private volatile List<Annotation> annotations;

    public InstanceMethod(ClassWrapper javaClass, Method method, Object object) {
        this.javaClass = checkNotNull(javaClass);
        this.method = checkNotNull(method);
        this.object = checkNotNull(object);
    }

    public InstanceMethod(
            ClassWrapper javaClass, Method method, Object object, List<Annotation> methodAnnotations) {
        this.javaClass = checkNotNull(javaClass);
        this.method = checkNotNull(method);
        this.object = checkNotNull(object);
        this.annotations = makeImmutable(checkNotNull(methodAnnotations));
    }

    public Object getObject() {
        return object;
    }

    /**
     * Gets the underlying Java method.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Gets a {@link ClassWrapper} for the class of the underlying Java method.
     */
    public ClassWrapper getJavaClass() {
        return javaClass;
    }

    /**
     * Gets the annotations on the underlying Java method.
     */
    public List<Annotation> getAnnotations() {
        if (annotations == null) {
            lock.lock();
            try {
                if (annotations == null) {
                    annotations = makeImmutable(asList(method.getAnnotations()));
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
