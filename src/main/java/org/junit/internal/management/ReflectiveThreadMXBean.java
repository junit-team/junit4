package org.junit.internal.management;

import org.junit.internal.Classes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Implementation of {@link ThreadMXBean} using the JVM reflectively.
 */
final class ReflectiveThreadMXBean implements ThreadMXBean {
  private final Object threadMxBean;


  private static final class Holder {
    static final Method getThreadCpuTimeMethod;
    static final Method isThreadCpuTimeSupportedMethod;

    private static final String FAILURE_MESSAGE = "Unable to access ThreadMXBean";

    static {
      Method threadCpuTime = null;
      Method threadCpuTimeSupported = null;
      try {
        Class<?> threadMXBeanClass = Classes.getClass("java.lang.management.ThreadMXBean");
        threadCpuTime = threadMXBeanClass.getMethod("getThreadCpuTime", long.class);
        threadCpuTimeSupported = threadMXBeanClass.getMethod("isThreadCpuTimeSupported");
      } catch (ClassNotFoundException e) {
        // do nothing, the methods will be null on failure
      } catch (NoSuchMethodException e) {
        // do nothing, the methods will be null on failure
      } catch (SecurityException e) {
        // do nothing, the methods will be null on failure
      }
      getThreadCpuTimeMethod = threadCpuTime;
      isThreadCpuTimeSupportedMethod = threadCpuTimeSupported;
    }
  }

  ReflectiveThreadMXBean(Object threadMxBean) {
    super();
    this.threadMxBean = threadMxBean;
  }

  /**
   * {@inheritDoc}
   */
  public long getThreadCpuTime(long id) {
    if (Holder.getThreadCpuTimeMethod != null) {
      Exception error = null;
      try {
        return (Long) Holder.getThreadCpuTimeMethod.invoke(threadMxBean, id);
      } catch (ClassCastException e) {
        error = e;
        // fallthrough
      } catch (IllegalAccessException e) {
        error = e;
        // fallthrough
      } catch (IllegalArgumentException e) {
        error = e;
        // fallthrough
      } catch (InvocationTargetException e) {
        error = e;
        // fallthrough
      }
      throw new UnsupportedOperationException(Holder.FAILURE_MESSAGE, error);
    }
    throw new UnsupportedOperationException(Holder.FAILURE_MESSAGE);
  }

  /**
   * {@inheritDoc}
   */
  public boolean isThreadCpuTimeSupported() {
    if (Holder.isThreadCpuTimeSupportedMethod != null) {
      try {
        return (Boolean) Holder.isThreadCpuTimeSupportedMethod.invoke(threadMxBean);
      } catch (ClassCastException e) {
        // fallthrough
      } catch (IllegalAccessException e) {
        // fallthrough
      } catch (IllegalArgumentException e) {
        // fallthrough
      } catch (InvocationTargetException e) {
        // fallthrough
      }
    }
    return false;
  }

}

