package org.junit.internal;

import java.io.Serializable;

/**
 * This class exists solely to provide a serializable description of a value to be serialized as a field in
 * {@link AssumptionViolatedException}. Being a {@link Throwable}, it is required to be {@link Serializable}, but a
 * value of type Object provides no guarantee to be serializable. This class works around that limitation as
 * {@link AssumptionViolatedException} only every uses the string representation of the value, while still retaining
 * backwards compatibility with classes compiled against its class signature before 4.14 and/or deserialization of
 * previously serialized instances.
 */
class SerializableValueDescription implements Serializable {
    private final String value;

    private SerializableValueDescription(Object value) {
        this.value = String.valueOf(value);
    }

    /**
     * Factory method that checks to see if the value is already serializable.
     * @param value the value to make serializable
     * @return The provided value if it is null or already serializable,
     * the SerializableValueDescription representation of it if it is not.
     */
    static Object asSerializableValue(Object value) {
        if (value == null || value instanceof Serializable) {
            return value;
        } else {
            return new SerializableValueDescription(value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
