package junit.runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class defines the current version of JUnit
 */
public class Version {
    private static final String VERSION;
    static {
        try {
            final Properties properties= new Properties();
            InputStream pomProps= getPomPropertiesAsStream("junit");
            if (pomProps == null) pomProps= getPomPropertiesAsStream("junit-dep");
            if (pomProps != null) properties.load(pomProps);
            VERSION= properties.getProperty("version", "<version>");
            if (pomProps != null) pomProps.close();
            assert !VERSION.equals("<version>") : "your class loader does not load resources at /META-INF/maven/junit/...";
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

	private Version() {
		// don't instantiate
	}

	public static String id() {
		return VERSION;
	}
	
	public static void main(String[] args) {
		System.out.println(id());
	}

    private static InputStream getPomPropertiesAsStream(String artifactId) {
        return Version.class.getResourceAsStream("/META-INF/maven/junit/" + artifactId + "/pom.properties");
    }
}
