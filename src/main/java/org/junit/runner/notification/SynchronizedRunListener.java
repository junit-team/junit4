package org.junit.runner.notification;

import java.lang.annotation.Annotation;

import net.jcip.annotations.ThreadSafe;

import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * SynchronizedRunListener decorates {@link RunListener} and
 * has all methods synchronized.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
@ThreadSafe
final class SynchronizedRunListener extends RunListener {
    private static final Object sMonitor = new Object();
    private final RunListener fListener;

    public static RunListener wrapIfNotThreadSafe(RunListener listener) {
        Class<? extends Annotation> annotation= getThreadSafeAnnotationClass();
        if (annotation == null) {
            return listener;
        }
        boolean isThreadSafe = listener.getClass().isAnnotationPresent(annotation);
        return isThreadSafe ? listener : new SynchronizedRunListener(listener);
    }

    SynchronizedRunListener(RunListener listener) {
        this.fListener = listener;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        synchronized (sMonitor) {
            fListener.testRunStarted(description);
        }
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        synchronized (sMonitor) {
            fListener.testRunFinished(result);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        synchronized (sMonitor) {
            fListener.testStarted(description);
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {
        synchronized (sMonitor) {
            fListener.testFinished(description);
        }
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        synchronized (sMonitor) {
            fListener.testFailure(failure);
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        synchronized (sMonitor) {
            fListener.testAssumptionFailure(failure);
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        synchronized (sMonitor) {
            fListener.testIgnored(description);
        }
    }

    @Override
    public int hashCode() {
        return fListener.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SynchronizedRunListener)) {
            return false;
        }
        SynchronizedRunListener that= (SynchronizedRunListener) other;
        
        return this.fListener.equals(that.fListener);
    }

    @Override
    public String toString() {
        return fListener.toString();
    }
    
    /**
     * Loads {@link net.jcip.annotations.ThreadSafe} via reflection. Uses the
     * Initialization on Demand Holder pattern (see
     * http://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html#dcl for details).
     */
    private static class ThreadSafeAnnotationHolder {
        private static Class<? extends Annotation> annotation = loadThreadSafeAnnotation();
        

        private static Class<? extends Annotation> loadThreadSafeAnnotation() {
            try {
                ClassLoader classLoader= Thread.currentThread().getContextClassLoader();
                Class<?> loadedAnnotation= classLoader.loadClass(
                        "net.jcip.annotations.ThreadSafe");
                return loadedAnnotation.asSubclass(Annotation.class);
            } catch (ClassNotFoundException e) {
                return null;
            } catch (ClassCastException e) {
                return null;
            }
        }
    }
 
    /**
     * Gets the {@link net.jcip.annotations.ThreadSafe} annotation if it is on
     * the classpath
     *
     * @return the annotation or {@code null} if it isn't on the classpath
     */
       static Class<? extends Annotation> getThreadSafeAnnotationClass() {
           return ThreadSafeAnnotationHolder.annotation;
       }
}
