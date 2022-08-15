package org.junit.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.manipulation.Alphanumeric;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.TestClass;

public class OrderWithValidatorTest {
    private final OrderWithValidator validator = new OrderWithValidator();

    @RunWith(JUnit4.class)
    @OrderWith(Alphanumeric.class)
    public static class TestWithNoValidationErrors {
        @Test
        public void passes() {}
    }

    @RunWith(JUnit4.class)
    @OrderWith(Alphanumeric.class)
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class TestAnnotatedWithFixMethodOrder {
        @Test
        public void passes() {}
    }

    @Test
    public void noErrorIsAddedForTestWithoutValdationErrors() {
        List<Exception> errors = validator.validateAnnotatedClass(
                new TestClass(TestWithNoValidationErrors.class));
        
        assertThat(errors.size(), is(0));
    }
 
    @Test
    public void errorIsAddedWhenTestAnnotatedWithFixMethodOrder() {
        List<Exception> errors = validator.validateAnnotatedClass(
                new TestClass(TestAnnotatedWithFixMethodOrder.class));
        
        assertThat(errors.size(), is(1));
        Exception exception = errors.get(0);
        assertThat(exception.getMessage(), is("@FixMethodOrder cannot be combined with @OrderWith"));
    }
}
