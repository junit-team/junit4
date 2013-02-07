// Copyright 2013 Google Inc. All Rights Reserved.

package org.junit.runner.notification;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for {@link SynchronizedRunListener}.
 * 
 * @author kcooney (Kevin Cooney)
 */
public class SynchronizedRunListenerTest {

    private static class MethodSignature {
        private final Method method;
        private final String name;
        private final List<Class<?>> parameterTypes;

        public MethodSignature(Method method) {
            this.method= method;
            name= method.getName();
            parameterTypes= Arrays.asList(method.getParameterTypes());
        }
        
        @Override
        public String toString() {
            return method.toString();
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MethodSignature)) {
                return false;
            }
            MethodSignature that = (MethodSignature) obj;
            return name.equals(that.name) && parameterTypes.equals(that.parameterTypes);
        }
    }

    private Set<MethodSignature> getAllDeclaredMethods(Class<?> type) {
        Set<MethodSignature> methods = new HashSet<MethodSignature>();
        for (Method method : type.getDeclaredMethods()) {
          methods.add(new MethodSignature(method));
        }
        return methods;
    }
    
    @Test
    public void overridesAllMethodsInRunListener() {
        Set<MethodSignature> runListenerMethods= getAllDeclaredMethods(RunListener.class);
        Set<MethodSignature> synchronizedRunListenerMethods= getAllDeclaredMethods(
                SynchronizedRunListener.class);

        assertTrue(synchronizedRunListenerMethods.containsAll(runListenerMethods));
    }

    private static class NamedListener extends RunListener {
        private final String name;

        public NamedListener(String name) {
            this.name= name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof NamedListener)) {
                return false;
            }
            NamedListener that= (NamedListener) obj;
            return this.name.equals(that.name);
        }
    }

    @Concurrent
    private static class ThreadSafeRunListener extends RunListener {
    }

    @Test
    public void namedListenerCorrectlyImplementsEqualsAndHashCode() {
        NamedListener listener1 = new NamedListener("blue");
        NamedListener listener2 = new NamedListener("blue");
        NamedListener listener3 = new NamedListener("red");
        
        assertTrue(listener1.equals(listener1));
        assertTrue(listener2.equals(listener2));
        assertTrue(listener3.equals(listener3));
        
        assertFalse(listener1.equals(null));
        assertFalse(listener1.equals(new Object()));

        assertTrue(listener1.equals(listener2));
        assertTrue(listener2.equals(listener1));
        assertFalse(listener1.equals(listener3));
        assertFalse(listener3.equals(listener1));

        assertEquals(listener1.hashCode(), listener2.hashCode());
        assertNotEquals(listener1.hashCode(), listener3.hashCode());
    }
    
    @Test
    public void equalsDelegates() {
        NamedListener listener1 = new NamedListener("blue");
        NamedListener listener2 = new NamedListener("blue");
        NamedListener listener3 = new NamedListener("red");
        
        assertEquals(new SynchronizedRunListener(listener1),
                new SynchronizedRunListener(listener1));
        assertEquals(new SynchronizedRunListener(listener1),
                new SynchronizedRunListener(listener2));
        assertNotEquals(new SynchronizedRunListener(listener1),
                new SynchronizedRunListener(listener3));
        assertNotEquals(new SynchronizedRunListener(listener1), listener1);
        assertNotEquals(listener1, new SynchronizedRunListener(listener1));
    }

    @Test
    public void hashCodeDelegates() {
        NamedListener listener = new NamedListener("blue");
        assertEquals(listener.hashCode(), new SynchronizedRunListener(listener).hashCode());
    }

    @Test
    public void wrapIfNotThreadSafeShouldNotWrapThreadSafeListeners() {
        ThreadSafeRunListener listener= new ThreadSafeRunListener();;
        assertSame(listener, SynchronizedRunListener.wrapIfNotThreadSafe(listener));
    }

    /**
     * Tests that {@link SynchronizedRunListener#wrapIfNotThreadSafe(RunListener)}
     * wraps listeners that are not thread-safe. This does not check if the
     * listener is synchronized; that is tested with the tests for {@link RunNotifier}.
     */
    @Test
    public void wrapIfNotThreadSafeShouldWrapNonThreadSafeListeners() {
        NamedListener listener= new NamedListener("name");
        RunListener wrappedListener= SynchronizedRunListener.wrapIfNotThreadSafe(listener);
        assertThat(wrappedListener, instanceOf(SynchronizedRunListener.class));
    }
}
