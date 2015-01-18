package org.junit.junitconsumer;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.lang.Throwable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;

public final class Activator implements BundleActivator {

    public void start(BundleContext bundleContext) throws Exception {
        System.setProperty("activatorFinishedSuccessfully", "false");

        HashMap<String, Bundle> bundles = new HashMap<String, Bundle>();
        for (Bundle bundle : bundleContext.getBundles()) {
            bundles.put(bundle.getSymbolicName(), bundle);
        }

        testOSGiSymbolicNames(bundles);
        testJUnitResources(bundles);
        testHamcrest();
        testWithJunitCore();

        System.setProperty("activatorFinishedSuccessfully", "true");
    }

    public void stop(BundleContext bundleContext) throws Exception {
    }

    private void testOSGiSymbolicNames(Map<String, Bundle> bundles) {
        String providerBundleSymbolicName = "org.junit";
        String consumerBundleSymbolicName = "consumer";
        assertThat(bundles.keySet(), hasItems(providerBundleSymbolicName, consumerBundleSymbolicName));
    }

    private void testJUnitResources(Map<String, Bundle> bundles) {
        assertNotNull(bundles.get("org.junit").getEntry("junit/runner/logo.gif"));
    }

    private void testHamcrest() {
        assertThat(Matcher.class.getClassLoader(), is(not(sameInstance(ClassLoader.getSystemClassLoader()))));
        assertThat(Matcher.class.getClassLoader(), is(not(sameInstance(Thread.currentThread().getContextClassLoader()))));
    }

    private void testWithJunitCore() {
        JUnitCore core = new JUnitCore();

        assertThat(core.getClass().getClassLoader(), is(not(sameInstance(ClassLoader.getSystemClassLoader()))));
        assertThat(core.getClass().getClassLoader(), is(not(sameInstance(Thread.currentThread().getContextClassLoader()))));

        Result result = core.run(TestClass.class);
        assertTrue(result.wasSuccessful());
        assertThat(result.getRunCount(), is(1));
    }

    public static class TestClass {
        @Test
        public void bundlesWorkWithHamcrest() {
            assertThat(TestClass.class.getSimpleName(), is("TestClass"));
        }
    }
}
