package org.junit.runner.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
        private final Method fMethod;
        private final String fName;
        private final List<Class<?>> fParameterTypes;

        public MethodSignature(Method method) {
            fMethod = method;
            fName = method.getName();
            fParameterTypes = Arrays.asList(method.getParameterTypes());
        }

        @Override
        public String toString() {
            return fMethod.toString();
        }

        @Override
        public int hashCode() {
            return fName.hashCode();
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
            return fName.equals(that.fName) && fParameterTypes.equals(that.fParameterTypes);
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
        Set<MethodSignature> runListenerMethods = getAllDeclaredMethods(RunListener.class);
        Set<MethodSignature> synchronizedRunListenerMethods = getAllDeclaredMethods(
                SynchronizedRunListener.class);

        assertTrue(synchronizedRunListenerMethods.containsAll(runListenerMethods));
    }

    private static class NamedListener extends RunListener {
        private final String fName;

        public NamedListener(String name) {
            fName = name;
        }

        @Override
        public String toString() {
          return "NamedListener";
        }

        @Override
        public int hashCode() {
            return fName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof NamedListener)) {
                return false;
            }
            NamedListener that = (NamedListener) obj;
            return this.fName.equals(that.fName);
        }
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
    public void toStringDelegates() {
        NamedListener listener = new NamedListener("blue");

        assertEquals("NamedListener", listener.toString());
        assertEquals("NamedListener (with synchronization wrapper)", wrap(listener).toString());
    }

    @Test
    public void equalsDelegates() {
        NamedListener listener1 = new NamedListener("blue");
        NamedListener listener2 = new NamedListener("blue");
        NamedListener listener3 = new NamedListener("red");

        assertEquals(wrap(listener1), wrap(listener1));
        assertEquals(wrap(listener1), wrap(listener2));
        assertNotEquals(wrap(listener1), wrap(listener3));
        assertNotEquals(wrap(listener1), listener1);
        assertNotEquals(listener1, wrap(listener1));
    }

    @Test
    public void hashCodeDelegates() {
        NamedListener listener = new NamedListener("blue");
        assertEquals(listener.hashCode(), wrap(listener).hashCode());
    }

    private SynchronizedRunListener wrap(RunListener listener) {
        return new SynchronizedRunListener(listener, this);
    }
}
