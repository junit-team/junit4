package org.junit.it;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
final class BundlesUtil {
    // Only mvn artifact jar files. Not the -javadoc.jar, -sources.jar, etc.
    static final String MAIN_MVN_JAR_NAME_PATTERN = "^(\\w+?(-)?(\\w+))+-(\\d+).(\\d+)?(.\\d+)(.jar|-SNAPSHOT.jar)$";

    private BundlesUtil() {
    }

    static Collection<String> relativePathFiles(final String fileNameRegex, String relativePath) {
        relativePath = relativePath.trim();
        File dir = new File(relativePath).getAbsoluteFile();
        File[] jars = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().matches(fileNameRegex);
            }
        });
        ArrayList<String> paths = new ArrayList<String>(jars.length);
        relativePath = relativePath.length() == 0 ? "" : relativePath + "/";
        for (File jar : jars) {
            paths.add(relativePath + jar.getName());
        }
        return paths;
    }

    static Collection<String> relativePathFiles(String fileNameRegex, String... relativePaths) {
        ArrayList<String> paths = new ArrayList<String>();
        for (String relativePath : relativePaths) {
            paths.addAll(relativePathFiles(fileNameRegex, relativePath));
        }
        return paths;
    }
}
