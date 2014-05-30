package org.junit.runner.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ServiceLoaderWrapper;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for the SPI integration of {@link org.junit.runner.notification.RunNotifier}.
 */
public class RunNotifierLoadListenersTest {

    private static final RunListener FIRST_LISTENER = new RunListener();

    private static final RunListener SECOND_LISTENER = new RunListener();

    private RunNotifier notifier;

    @Before
    public void setUp() throws Exception {
        notifier = new RunNotifier(new FakeServiceLoaderWrapper());
        notifier.loadListeners();
    }

    @Test
    public void countListenersTest() throws Exception {
        assertThat(notifier.getListeners().size(), is(2));
    }

    @Test
    public void firstListenerLoadedTest() throws Exception {
        assertThat(notifier.getListeners(), hasItem(FIRST_LISTENER));
    }

    @Test
    public void secondListenerLoadedTest() throws Exception {
        assertThat(notifier.getListeners(), hasItem(SECOND_LISTENER));
    }


    private class FakeServiceLoaderWrapper extends ServiceLoaderWrapper {
        @Override
        public <T> List<T> load(Class<T> serviceType) {
            return Arrays.asList(serviceType.cast(FIRST_LISTENER), serviceType.cast(SECOND_LISTENER));
        }
    }

}
