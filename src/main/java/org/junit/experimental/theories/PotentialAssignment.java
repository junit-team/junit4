package org.junit.experimental.theories;

import static java.lang.String.format;

public abstract class PotentialAssignment {
    public static class CouldNotGenerateValueException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public CouldNotGenerateValueException() {
        }
        
        public CouldNotGenerateValueException(Throwable e) {
            super(e);
        }
    }

    public static PotentialAssignment forValue(final String name, final Object value) {
        return new PotentialAssignment() {
            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public String toString() {
                return format("[%s]", value);
            }

            @Override
            public String getDescription() {
                String valueString;

                if (value == null) {
                    valueString = "null";
                } else {
                    try {
                        valueString = format("\"%s\"", value);
                    } catch (Throwable e) {
                        valueString = format("[toString() threw %s: %s]", 
                                             e.getClass().getSimpleName(), e.getMessage());
                    }
                }

                return format("%s <from %s>", valueString, name);
            }
        };
    }

    public abstract Object getValue() throws CouldNotGenerateValueException;

    public abstract String getDescription() throws CouldNotGenerateValueException;
}