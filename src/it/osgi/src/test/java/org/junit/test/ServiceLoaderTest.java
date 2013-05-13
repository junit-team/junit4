package org.junit.test;

import org.junit.Test;
import org.junit.it.ServiceLoader;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public class ServiceLoaderTest {
    @Test
    public void serviceLoader() throws Exception {
        ServiceLoader<Service> loader = ServiceLoader.load(Service.class);
        assertFalse(loader.isEmpty());
        Iterator<Service> it = loader.iterator();
        assertTrue(it.hasNext());
        assertThat(it.next(), instanceOf(ServiceA.class));
        assertTrue(it.hasNext());
        assertThat(it.next(), instanceOf(ServiceB.class));
        assertFalse(it.hasNext());
    }
}
