package org.junit.tests.experimental.theories.extendingwithstubs;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.hamcrest.BaseDescription;
import org.hamcrest.Description;
import org.junit.internal.AssumptionViolatedException;

public class Guesser<T> extends ReguessableValue {
    static class GuessMap extends HashMap<MethodCall, Object> implements
            InvocationHandler {
        private static final long serialVersionUID = 1L;

        public GuessMap(GuessMap guesses) {
            super(guesses);
        }

        public GuessMap() {
        }

        GuessMap replaceGuess(Object oldValue, Object newValue) {
            GuessMap newGuesses = new GuessMap(this);
            for (Entry<MethodCall, Object> entry : newGuesses.entrySet()) {
                if (entry.getValue().equals(oldValue)) {
                    entry.setValue(newValue);
                }
            }
            return newGuesses;
        }

        protected Object generateGuess(Class<?> returnType) {
            if (returnType.equals(String.class)) {
                return "GUESS" + new Random().nextInt();
            }
            if (returnType.equals(Integer.class)
                    || returnType.equals(int.class)) {
                return new Random().nextInt();
            }
            return null;
        }

        Object getGuess(MethodCall call) {
            if (!containsKey(call)) {
                put(call, generateGuess(call.getReturnType()));
            }
            return get(call);
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            return getGuess(new MethodCall(method, args));
        }
    }

    private final GuessMap guesses;

    private final Class<? extends T> type;

    public Guesser(Class<? extends T> type) {
        this(type, new GuessMap());
    }

    public Guesser(Class<? extends T> type2, GuessMap guesses) {
        this.type = type2;
        this.guesses = guesses;
    }

    @SuppressWarnings("unchecked")
    public T getProxy() {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{getType()}, guesses);
    }

    @Override
    public List<ReguessableValue> reguesses(AssumptionViolatedException e) {
        final ArrayList<ReguessableValue> returnThis = new ArrayList<ReguessableValue>();
        e.describeTo(new BaseDescription() {
            @Override
            protected void append(char arg0) {
            }

            boolean expectedSeen = false;
            Object expected = null;

            @Override
            public Description appendValue(Object value) {
                noteValue(value);
                return super.appendValue(value);
            }

            private void noteValue(Object value) {
                if (!expectedSeen) {
                    expected = value;
                    expectedSeen = true;
                    return;
                }

                GuessMap newGuesses = guesses.replaceGuess(expected, value);
                returnThis.add(new Guesser<T>(getType(), newGuesses));
            }
        });
        return returnThis;
    }

    @Override
    public Object getValue() throws CouldNotGenerateValueException {
        return getProxy();
    }

    public Class<? extends T> getType() {
        return type;
    }

    @Override
    public String getDescription() throws CouldNotGenerateValueException {
        return "guesser[" + type + "]";
    }

}