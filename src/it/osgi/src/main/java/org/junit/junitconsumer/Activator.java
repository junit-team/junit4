package org.junit.junitconsumer;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;

/**
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public final class Activator implements BundleActivator {

    public void start(BundleContext bundleContext) throws Exception {
        System.setProperty("activatorFinishedSuccessfully", "false");

        HashMap<String, Bundle> bundles = new HashMap<String, Bundle>();
        for (Bundle bundle : bundleContext.getBundles()) {
            bundles.put(bundle.getSymbolicName(), bundle);
        }

        testHamcrest();
        testSymbolicNames(bundles);
        testResources(bundles);
        testWithJunitCore();
        testInternalPackages();

        System.setProperty("activatorFinishedSuccessfully", "true");
    }

    public void stop(BundleContext bundleContext) throws Exception {
    }

    private void testHamcrest() {
    // The hamcrest-core:1.3 is not yet OSGi bundle, thus the library has to appear in application ClassLoader.
    assertThat(Matcher.class.getClassLoader(), is(sameInstance(ClassLoader.getSystemClassLoader())));
    }

    private void testSymbolicNames(Map<String, Bundle> bundles) {
        String providerBundleSymbolicName = "org.junit";
        String consumerBundleSymbolicName = "consumer";
        assertThat(bundles.keySet(), hasItems(providerBundleSymbolicName, consumerBundleSymbolicName));
    }

    private void testResources(Map<String, Bundle> bundles) {
        assertNotNull(bundles.get("org.junit").getEntry("junit/runner/logo.gif"));
    }

    private void testWithJunitCore() {
        JUnitCore core = new JUnitCore();

        assertThat(core.getClass().getClassLoader(), is(not(sameInstance(ClassLoader.getSystemClassLoader()))));
        assertThat(core.getClass().getClassLoader(), is(not(sameInstance(Thread.currentThread().getContextClassLoader()))));

        Result result = core.run(TestClass.class);
        assertTrue(result.wasSuccessful());
        assertThat(result.getRunCount(), is(1));
    }

    private void testInternalPackages() {
        try {
            Class.forName("org.junit.internal.JUnitSystem");
            fail("internal package 'org.junit.internal' should not be exported from OSGi bundle");
        } catch (ClassNotFoundException e) {
            // do nothing -expected
        }

        try {
            Class.forName("org.junit.experimental.theories.internal.AllMembersSupplier");
            fail("internal package 'org.junit.experimental.theories.internal' should not be exported from OSGi bundle");
        } catch (ClassNotFoundException e) {
            // do nothing -expected
        }
    }

    public static class TestClass {
        @Test
        public void test() {
            // bundle works with Hamcrest
            assertThat("", is(anything()));
        }
    }
}
