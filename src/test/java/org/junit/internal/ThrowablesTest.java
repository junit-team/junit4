package org.junit.internal;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThrowablesTest {

    @Test
    public void testGetStackTrace() {
        Throwable t = new Throwable("message");
        assertThat(Throwables.getStackTrace(t), startsWith("java.lang.Throwable: message\n" +
                        "\tat org.junit.internal.ThrowablesTest.testGetStackTrace(ThrowablesTest.java:"));
    }
}