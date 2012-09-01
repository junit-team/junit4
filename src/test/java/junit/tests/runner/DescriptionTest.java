package junit.tests.runner;

import java.text.Annotation;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.Description;

public class DescriptionTest {

	@Test public void testIsSuite() {
		Description d = Description.createSuiteDescription("SuiteName", new Annotation[0]);
		Assert.assertTrue(d.isSuite());
		Description m = Description.createTestDescription("TestClass", "testMethod", new Annotation[0]);
		d.addChild(m);
		Assert.assertTrue(d.isSuite());
	}
}
