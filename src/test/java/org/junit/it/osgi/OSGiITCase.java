package org.junit.it.osgi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public class OSGiITCase {
    private final Collection<Bundle> bundles = new ArrayList<Bundle>();
    private Framework framework;

    @Before
    public void init() throws Exception {
        // Load a framework factory
        FrameworkFactory frameworkFactory = ServiceLoader.loadFirst(FrameworkFactory.class);
        // Create a framework
        Map<String, String> config = new HashMap<String, String>();
        // OSGi stores its persistent data:
        config.put(Constants.FRAMEWORK_STORAGE, new File("target/osgi-cache").getCanonicalPath());
        // Request OSGi to clean its storage area on startup
        config.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
        config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.hamcrest,org.hamcrest.core");
        framework = frameworkFactory.newFramework(config);
        framework.init();
    }

    @After
    public void deinit() throws Exception {
        for (Bundle bundle : bundles) {
            bundle.stop();
        }

        framework.stop();
        framework.waitForStop(0);
    }

    @Test
    public void testOSGi() throws Exception {
        BundleContext context = framework.getBundleContext();
        for (String bundle : System.getProperty("bundles").split(",")) {
            bundles.add(context.installBundle(bundle.trim()));
        }
        framework.start();
        for (Bundle bundle : bundles) {
            bundle.start();
        }
    }

    /**
     * The hamcrest-core:1.3 is not yet OSGi bundle, thus the library has to appear in application ClassLoader.
     * @see Constants#FRAMEWORK_SYSTEMPACKAGES_EXTRA
     */
    @Test
    public void testFailsafePluginSettings() throws ClassNotFoundException {
        Thread.currentThread().getContextClassLoader().loadClass("org.hamcrest.Matcher");
    }
}
