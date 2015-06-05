package org.junit.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.it.BundlesUtil.relativePathJarFiles;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

/**
 * Using the pure OSGi framework API, the JUnit bundle is tested.
 * If the consumer's Activator fails, the start method on consumer bundle throws BundleException.
 * Log file is target/it/osgi/build.log.
 */
@RunWith(Parameterized.class)
public class FrameworkIT {

    @Parameters
    public static Iterable<FrameworkFactory[]> factories() throws Exception {
        ArrayList<FrameworkFactory[]> factories = new ArrayList<FrameworkFactory[]>();
        for (FrameworkFactory factory : ServiceLoader.load(FrameworkFactory.class)) {
            factories.add(new FrameworkFactory[] {factory});
        }
        return factories;
    }

    @Parameter
    public FrameworkFactory frameworkFactory;

    private Framework framework;

    @Before
    public void init() throws Exception {
        // Create a framework
        Map<String, String> config = new HashMap<String, String>();
        // OSGi stores its persistent data:
        config.put(Constants.FRAMEWORK_STORAGE, new File("target/osgi-cache").getCanonicalPath());
        // Request OSGi to clean its storage area on startup
        config.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
        framework = frameworkFactory.newFramework(config);
        framework.init();
    }

    @After
    public void deinit() throws Exception {
        BundleContext context = framework.getBundleContext();
        for (Bundle bundle : context.getBundles()) {
            bundle.stop();
        }

        framework.stop();
        framework.waitForStop(0);
    }

    @Test
    public void testOSGi() throws Throwable {
        System.setProperty("activatorFinishedSuccessfully", "false");
        BundleContext context = framework.getBundleContext();
        for (String bundle : relativePathJarFiles("target/bundles", "target")) {
            context.installBundle("file:" + bundle);
        }

        framework.start();

        for (Bundle bundle : context.getBundles()) {
            bundle.start();
        }

        assertTrue("Activator failed in a test.", Boolean.getBoolean("activatorFinishedSuccessfully"));
    }
}
