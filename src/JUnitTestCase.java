import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Correct structure:
 *
 * JUnitTestCase
 *  |
 *  |- JUnitTestCase.MyTestSuite
 *  |   |
 *  |   |- JUnitTestCase.MyTestSuite.MyTestCase
 *  |       |
 *  |       |- test1
 *  |       |
 *  |       |- test2
 *  |
 *  |- JUnitTestCase.StopTest
 *      |
 *      |- testStop
 *
 * Wrong structure when test.MyTestSuite is created using JUnit4TestAdapter
 *
 * JUnitTestCase
 *  |
 *  |- JUnitTestCase.MyTestSuite
 *  |   |
 *  |   |- JUnitTestCase.MyTestSuite.MyTestCase
 *  |   |   |
 *  |   |   |- test1
 *  |   |   |
 *  |   |   |- test2
 *  |   |
 *  x   |- JUnitTestCase.StopTest
 *      |
 *      |- testStop
 */
public final class JUnitTestCase
{
	public static void buildUserTests(TestSuite suite)
	{
		// Cause a problem because there are 2 tests
		suite.addTest(new JUnit4TestAdapter(MyTestSuite.class));

		// This works with 2 tests or more
//		suite.addTest(MyTestSuite.suite());
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(JUnitTestCase.class.getName());
		buildUserTests(suite);
		suite.addTest(new TestSuite(StopTest.class));
		return suite;
	}

	public static class MyTestCase
	{
		@org.junit.Test
		public void test1()
		{
		}

		@org.junit.Test
		public void test2()
		{
		}
	}

	@SuiteClasses
	({
		MyTestCase.class,
	})
	@RunWith(Suite.class)
	public static class MyTestSuite
	{
		public static Test suite()
		{
			TestSuite suite = new TestSuite(MyTestSuite.class.getName());
			suite.addTest(new JUnit4TestAdapter(MyTestCase.class));
			return suite;
		}
	}

	public static class StopTest extends TestCase
	{
		public void testStop() throws Exception
		{
		}
	}
}
