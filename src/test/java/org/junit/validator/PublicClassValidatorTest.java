package org.junit.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runners.model.TestClass;

public class PublicClassValidatorTest {
    private final PublicClassValidator validator = new PublicClassValidator();

    public static class PublicClass {

    }

    @Test
    public void acceptsPublicClass() {
        TestClass testClass = new TestClass(PublicClass.class);
        List<Exception> validationErrors = validator
                .validateTestClass(testClass);
        assertThat(validationErrors,
                is(equalTo(Collections.<Exception> emptyList())));
    }

    static class NonPublicClass {

    }

    @Test
    public void rejectsNonPublicClass() {
        TestClass testClass = new TestClass(NonPublicClass.class);
        List<Exception> validationErrors = validator
                .validateTestClass(testClass);
        assertThat("Wrong number of errors.", validationErrors.size(),
                is(equalTo(1)));
    }
}
