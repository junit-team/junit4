package junit.runner;

import java.util.Enumeration;
import junit.swingui.TestSelector;


/**
 * Collects Test class names to be presented
 * by the TestSelector. 
 * @see TestSelector
 */
public interface TestCollector {
	/**
	 * Returns an enumeration of Strings with qualified class names
	 */
	public Enumeration collectTests();
}
