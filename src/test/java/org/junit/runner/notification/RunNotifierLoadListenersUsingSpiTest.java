package org.junit.runner.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ServiceLoaderWrapper;

import java.net.URL;
import java.net.URLClassLoader;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for the SPI integration of {@link org.junit.runner.notification.RunNotifier}.
 */
public class RunNotifierLoadListenersUsingSpiTest {

    private RunNotifier notifier;

    @Before
    public void setUp() throws Exception {
        URL url = getClass().getClassLoader().getResource("testdata/");
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());

        notifier = new RunNotifier(new ServiceLoaderWrapper(classLoader));
        notifier.loadListeners();
    }

    @Test
    public void countListenersTest() throws Exception {
        assertThat(notifier.getListeners().size(), is(1));
    }
}
