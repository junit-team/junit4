package junit.tests.runner;

import junit.framework.TestCase;
import junit.runner.SimpleTestCollector;

public class SimpleTestCollectorTest extends TestCase {
	
	public void testMissingDirectory() {
		SimpleTestCollector collector= new SimpleTestCollector();
		assertFalse(collector.collectFilesInPath("foobar").elements().hasMoreElements());
	}

}

