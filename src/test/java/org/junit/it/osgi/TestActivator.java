package org.junit.it.osgi;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.runner.JUnitCore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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
        // The hamcrest-core:1.3 is not yet OSGi bundle, thus the library has to appear in application ClassLoader.
        assertThat(Matcher.class.getClassLoader(), is(sameInstance(ClassLoader.getSystemClassLoader())));

        String providerBundleSymbolicName = "org.junit";
        String consumerBundleSymbolicName = "org.junit.it.osgi";
        ArrayList<String> symbolicNames = new ArrayList<String>();
        for (Bundle bundle : bundleContext.getBundles()) {
            symbolicNames.add(bundle.getSymbolicName());
        }
        Assert.assertThat(symbolicNames, hasItems(providerBundleSymbolicName, consumerBundleSymbolicName));

        JUnitCore core = new JUnitCore();

        assertNotNull(core);
        assertThat(core.getClass().getClassLoader(), is(not(sameInstance(ClassLoader.getSystemClassLoader()))));
        assertThat(core.getClass().getClassLoader(), is(not(sameInstance(Thread.currentThread().getContextClassLoader()))));
        assertNull(core.getClass().getClassLoader().getParent());
    }

    public void stop(BundleContext bundleContext) throws Exception {
    }
}
