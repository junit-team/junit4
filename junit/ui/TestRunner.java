package junit.ui;

import junit.framework.*;
import junit.runner.*;
 
/**
 * @deprecated use junit.awtui.TestRunner.
 */
 public class TestRunner extends junit.awtui.TestRunner {
	public static void main(String[] args) {
		new TestRunner().start(args);
	}
}