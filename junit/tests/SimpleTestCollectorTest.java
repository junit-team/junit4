package junit.tests;

import junit.framework.TestCase;
import junit.runner.SimpleTestCollector;

public class SimpleTestCollectorTest extends TestCase {

	/**
	 * Constructor for ClassPathTestCollectorTest
	 */
	public SimpleTestCollectorTest(String name) {
		super(name);
	}
	
	public void testMissingDirectory() {
		SimpleTestCollector collector= new SimpleTestCollector();
		assertFalse(collector.collectFilesInPath("foobar").elements().hasMoreElements());
	}

}

