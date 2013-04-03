package org.junit.osgi;

import org.junit.runner.JUnitCore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Activates and deactivates OSGi bundle.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public final class Activator implements BundleActivator {
    private final AtomicReference<ServiceRegistration> registration = new AtomicReference<ServiceRegistration>();

    /**
     * @param bundleContext the framework context for the junit bundle.
     */
    public void start(BundleContext bundleContext) throws Exception {
        registration.set(bundleContext.registerService(JUnitCore.class, new JUnitCore(), null));
    }

    /**
     * @param bundleContext the framework context for the junit bundle.
     */
    public void stop(BundleContext bundleContext) throws Exception {
        ServiceRegistration registration = this.registration.getAndSet(null);
        if (registration != null) {
            registration.unregister();
        }
    }
}
