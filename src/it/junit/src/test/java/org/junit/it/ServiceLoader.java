package org.junit.it;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Substitutes Java 1.6 ServiceLoader. Used in integration test {@link FrameworkIT}.
 * <p/>Loads OSGi frameworks.
 *
 * @author <a href="mailto:tibor.digana@gmail.com">Tibor Digana (tibor17)</a>
 * @since 4.12
 */
public class ServiceLoader<S> implements Iterable<S> {
    private final Collection<S> services = new ArrayList<S>();

    private ServiceLoader() {
    }

    private void addService(S service) {
        services.add(service);
    }

    public static <S> ServiceLoader<S> load(Class<S> clazz) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = cl.getResources("META-INF/services/" + clazz.getName());
        ServiceLoader<S> serviceLoader = new ServiceLoader<S>();
        for (URL url; urls.hasMoreElements(); ) {
            url = urls.nextElement();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(url.openStream()));
                for (String s; (s = br.readLine()) != null; ) {
                    s = s.trim();
                    // Try to load non-empty, non-commented line.
                    if (s.length() > 0 && s.charAt(0) != '#') {
                        S service = clazz.cast(Class.forName(s).newInstance());
                        serviceLoader.addService(service);
                    }
                }
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return serviceLoader;
    }

    public Iterator<S> iterator() {
        return services.iterator();
    }

    public boolean isEmpty() {
        return services.isEmpty();
    }
}
