package org.junit.it.osgi;

import org.junit.Assert;
import org.junit.runner.JUnitCore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;

/**
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public final class TestActivator implements BundleActivator {
    public void start(BundleContext bundleContext) throws Exception {
        String providerBundleSymbolicName = "org.junit";
        String consumerBundleSymbolicName = "org.junit.it.osgi";
        ArrayList<String> symbolicNames = new ArrayList<String>();
        for (Bundle bundle : bundleContext.getBundles()) {
            symbolicNames.add(bundle.getSymbolicName());
        }
        Assert.assertThat(symbolicNames, hasItems(providerBundleSymbolicName, consumerBundleSymbolicName));

        ServiceReference<JUnitCore> ref = bundleContext.getServiceReference(JUnitCore.class);
        JUnitCore core = bundleContext.getService(ref);

        try {
            assertNotNull(core);
            assertThat(core.getClass().getClassLoader(), is(not(sameInstance(ClassLoader.getSystemClassLoader()))));
            assertThat(core.getClass().getClassLoader(), is(not(sameInstance(Thread.currentThread().getContextClassLoader()))));
            assertNull(core.getClass().getClassLoader().getParent());
        } finally {
            bundleContext.ungetService(ref);
        }
    }

    public void stop(BundleContext bundleContext) throws Exception {
    }
}
