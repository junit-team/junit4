package org.junit.runners.parameterized;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.TestClass;

/**
 * A {@code TestWithParameters} keeps the data together that are needed for
 * creating a runner for a single data set of a parameterized test. It has a
 * name, the test class and a list of parameters.
 * 
 * @since 4.12
 */
public class TestWithParameters {
    private final String fName;

    private final TestClass fTestClass;

    private final List<Object> fParameters;

    public TestWithParameters(String name, TestClass testClass,
            List<Object> parameters) {
        notNull(name, "The name is missing.");
        notNull(testClass, "The test class is missing.");
        notNull(parameters, "The parameters are missing.");
        fName = name;
        fTestClass = testClass;
        fParameters = unmodifiableList(new ArrayList<Object>(parameters));
    }

    public String getName() {
        return fName;
    }

    public TestClass getTestClass() {
        return fTestClass;
    }

    public List<Object> getParameters() {
        return fParameters;
    }

    @Override
    public int hashCode() {
        int prime = 14747;
        int result = prime + fName.hashCode();
        result = prime * result + fTestClass.hashCode();
        return prime * result + fParameters.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TestWithParameters other = (TestWithParameters) obj;
        return fName.equals(other.fName)
                && fParameters.equals(other.fParameters)
                && fTestClass.equals(other.fTestClass);
    }

    @Override
    public String toString() {
        return fTestClass.getName() + " '" + fName + "' with parameters "
                + fParameters;
    }

    private static void notNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
