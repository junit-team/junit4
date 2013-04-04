package org.junit.it.osgi;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertTrue;

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
        HashMap<String, Bundle> bundles = new HashMap<String, Bundle>();
        for (Bundle bundle : bundleContext.getBundles()) {
            bundles.put(bundle.getSymbolicName(), bundle);
        }
        assertThat(bundles.keySet(), hasItems(providerBundleSymbolicName, consumerBundleSymbolicName));

        assertNotNull(bundles.get("org.junit").getEntry("junit/runner/logo.gif"));

        JUnitCore core = new JUnitCore();

        assertThat(core.getClass().getClassLoader(), is(not(sameInstance(ClassLoader.getSystemClassLoader()))));
        assertThat(core.getClass().getClassLoader(), is(not(sameInstance(Thread.currentThread().getContextClassLoader()))));
        assertNull(core.getClass().getClassLoader().getParent());

        Result result = core.run(TestClass.class);
        assertTrue(result.wasSuccessful());
        assertThat(result.getRunCount(), is(1));
    }

    public void stop(BundleContext bundleContext) throws Exception {
    }

    public static class TestClass {
        @Test
        public void test() {
            // bundle works with Hamcrest
            assertThat("", is(anything()));
        }
    }
}
