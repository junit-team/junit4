package org.junit.it;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:tibor.digana@gmail.com">Tibor Digana (tibor17)</a>
 * @since 4.12
 */
final class BundlesUtil {
    private BundlesUtil() {
        throw new IllegalStateException("Not instantiated constructor.");
    }

    static Collection<String> relativePathJarFiles(String... relativePaths) {
        ArrayList<String> paths = new ArrayList<String>();
        for (String relativePath : relativePaths) {
            paths.addAll(relativePathJarFiles(relativePath));
        }
        return paths;
    }

    private static Collection<String> relativePathJarFiles(String relativePath) {
        relativePath = relativePath.trim();
        File dir = new File(relativePath).getAbsoluteFile();
        File[] jars = dir.listFiles(new FileFilter() {
            public boolean accept(File pathName) {
                return pathName.isFile() && pathName.getName().endsWith(".jar");
            }
        });
        ArrayList<String> paths = new ArrayList<String>(jars.length);
        relativePath = relativePath.length() == 0 ? "" : relativePath + "/";
        for (File jar : jars) {
            paths.add(relativePath + jar.getName());
        }
        return paths;
    }
}
