package org.junit.internal;

import java.io.Serializable;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

/**
 * This class exists solely to provide a serializable description of a matcher to be serialized as a field in
 * {@link AssumptionViolatedException}. Being a {@link Throwable}, it is required to be {@link Serializable}, but most
 * implementations of {@link Matcher} are not. This class works around that limitation as
 * {@link AssumptionViolatedException} only every uses the description of the {@link Matcher}, while still retaining
 * backwards compatibility with classes compiled against its class signature before 4.14 and/or deserialization of
 * previously serialized instances.
 */
class SerializableMatcherDescription<T> extends BaseMatcher<T> implements Serializable {

    private final String matcherDescription;

    private SerializableMatcherDescription(Matcher<T> matcher) {
        matcherDescription = StringDescription.asString(matcher);
    }

    public boolean matches(Object o) {
        throw new UnsupportedOperationException("This Matcher implementation only captures the description");
    }

    public void describeTo(Description description) {
        description.appendText(matcherDescription);
    }

    /**
     * Factory method that checks to see if the matcher is already serializable.
     * @param matcher the matcher to make serializable
     * @return The provided matcher if it is null or already serializable,
     * the SerializableMatcherDescription representation of it if it is not.
     */
    static <T> Matcher<T> asSerializableMatcher(Matcher<T> matcher) {
        if (matcher == null || matcher instanceof Serializable) {
            return matcher;
        } else {
            return new SerializableMatcherDescription<T>(matcher);
        }
    }
}
