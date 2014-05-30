package org.junit.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Load services by specified service type using Java SPI.
 *
 * @since 4.12
 */
public class ServiceLoaderWrapper {

    private final ClassLoader classLoader;

    /**
     * Create instance with specified class loader.
     */
    public ServiceLoaderWrapper(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Create instance with current thread class loader.
     */
    public ServiceLoaderWrapper() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Invoke to find all services for given service type
     *
     * @param serviceType specified service type
     * @return List of found services
     */
    public <T> List<T> load(Class<T> serviceType) {
        List<T> foundServices = new ArrayList<T>();
        Iterator<T> iterator = ServiceLoader.load(serviceType, classLoader).iterator();

        while (checkHasNextSafely(iterator)) {
            try {
                T item = iterator.next();
                foundServices.add(item);
            } catch (ServiceConfigurationError e) {
                System.err.println(e.getMessage());
            }
        }
        return foundServices;
    }

    /**
     * Check {@link java.util.Iterator#hasNext()} safely.
     *
     * @param iterator specified Iterator to check hasNext
     * @return true if {@link java.util.Iterator#hasNext()} checked successfully, false otherwise.
     */
    private boolean checkHasNextSafely(Iterator iterator) {
        try {
            /* Throw a ServiceConfigurationError if a provider-configuration file violates the specified format,
            or if it names a provider class that cannot be found and instantiated, or if the result of
            instantiating the class is not assignable to the service type, or if any other kind of exception
            or error is thrown as the next provider is located and instantiated.
            @see http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html#iterator()
            */
            return iterator.hasNext();
        } catch (ServiceConfigurationError e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
