package org.junit.tests.listening;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class ExternalListenerTest {
	public static final class CustomListener extends RunListener {
		boolean testRunStarted;
		public CustomListener() { externalListener = this; }
		@Override public void testRunStarted(Description description) { testRunStarted = true; }
	}

	private static CustomListener externalListener;

	public static final class OneTest {
		@Test
		public void nothing() {}
	}

	@BeforeClass
	public static void createServiceProviderConfigurationFile() throws Exception {
		URL location = CustomListener.class.getProtectionDomain().getCodeSource().getLocation();
		File file = new File(location.getPath() + "META-INF/services/" + RunListener.class.getName());
		file.getParentFile().mkdirs();

		Writer output = new FileWriter(file);
		output.write(CustomListener.class.getName());
		output.close();

		file.deleteOnExit();
	}

	@Test
	public void publishTestRunEventsToExternalRunListener() {
		new RunNotifier().fireTestRunStarted(null);

		assertNotNull("External RunListener not loaded", externalListener);
		assertTrue("Missing event on external RunListener", externalListener.testRunStarted);
	}
}
