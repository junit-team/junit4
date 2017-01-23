package org.junit.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class RequestTest {

    /**
     * #1320 A root of a {@link Description} produced by
     * {@link Request#classes(Class...)} should be named "classes"
     */
    @Test
    public void createsADescriptionWithANameForClasses() {
        Description description = Request
                .classes(RequestTest.class, RequestTest.class).getRunner()
                .getDescription();
        assertThat(description.toString(), is("classes"));
    }
}
