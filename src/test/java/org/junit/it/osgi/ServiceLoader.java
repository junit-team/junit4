package org.junit.it.osgi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Substitutes Java 1.6 ServiceLoader. Used in integration test {@link IntegrationTestCase}.
 * Loads OSGi framework as a service implemented by some OSGi vendor - currently Felix.
 * The vendor is specified in pom.xml dependencies.
 * Current dependency is selected by groupId: org.apache.felix and artifactId: org.apache.felix.framework.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
class ServiceLoader {
    static <S> S loadFirst(Class<S> clazz) {
        URL url = clazz.getClassLoader().getResource("META-INF/services/".concat(clazz.getName()));
        if (url != null) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(url.openStream()));
                for (String s; (s = br.readLine()) != null; ) {
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if (s.length() > 0 && s.charAt(0) != '#') {
                        return clazz.cast(Class.forName(s).newInstance());
                    }
                }
                return null;
            } catch (Exception e) {
                throw new Error(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        } else {
            return null;
        }
    }
}
