package org.junit.tests.running.methods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class TestMethodTest {

	@SuppressWarnings("all")  
	public static class EverythingWrong {
		private EverythingWrong() {}
		@BeforeClass public void notStaticBC() {}
		@BeforeClass static void notPublicBC() {}
		@BeforeClass public static int nonVoidBC() { return 0; }
		@BeforeClass public static void argumentsBC(int i) {}
		@BeforeClass public static void fineBC() {}
		@AfterClass public void notStaticAC() {}
		@AfterClass static void notPublicAC() {}
		@AfterClass public static int nonVoidAC() { return 0; }
		@AfterClass public static void argumentsAC(int i) {}
		@AfterClass public static void fineAC() {}
		@After public static void staticA() {}
		@After void notPublicA() {}
		@After public int nonVoidA() { return 0; }
		@After public void argumentsA(int i) {}
		@After public void fineA() {}
		@Before public static void staticB() {}
		@Before void notPublicB() {}
		@Before public int nonVoidB() { return 0; }
		@Before public void argumentsB(int i) {}
		@Before public void fineB() {}
		@Test public static void staticT() {}
		@Test void notPublicT() {}
		@Test public int nonVoidT() { return 0; }
		@Test public void argumentsT(int i) {}
		@Test public void fineT() {}
	}
	
	@Test public void testFailures() throws Exception {
		List<Throwable> problems= validateAllMethods(EverythingWrong.class);
		int errorCount= 1 + 4 * 5; // missing constructor plus four invalid methods for each annotation */
		assertEquals(errorCount, problems.size());
	}

	static public class SuperWrong {
		@Test void notPublic() {
		}
	}

	static public class SubWrong extends SuperWrong {
		@Test public void justFine() {
		}
	}

	@Test public void validateInheritedMethods() throws Exception {
		List<Throwable> problems= validateAllMethods(SubWrong.class);
		assertEquals(1, problems.size());
	}

	static public class SubShadows extends SuperWrong {
		@Override
		@Test public void notPublic() {
		}
	}

	@Test public void dontValidateShadowedMethods() throws Exception {
		List<Throwable> problems= validateAllMethods(SubShadows.class);
		assertTrue(problems.isEmpty());
	}

	private List<Throwable> validateAllMethods(Class<?> clazz) {
		try {
			new BlockJUnit4ClassRunner(clazz);
		} catch (InitializationError e) {
			return e.getCauses();
		}
		return Collections.emptyList();
	}

	static public class IgnoredTest {
		@Test public void valid() {}
		@Ignore @Test public void ignored() {}
		@Ignore("For testing purposes") @Test public void withReason() {}
	}

	@Test public void ignoreRunner() {
		JUnitCore runner= new JUnitCore();
		Result result= runner.run(IgnoredTest.class);
		assertEquals(2, result.getIgnoreCount());
		assertEquals(1, result.getRunCount());
	}

	@Test public void compatibility() {
		TestResult result= new TestResult();
		new JUnit4TestAdapter(IgnoredTest.class).run(result);
		assertEquals(1, result.runCount());
	}
	
	public static class Confused {
		@Test public void a(Object b) {
		}
		
		@Test public void a() {
		}
	}
	
	@Test(expected=InitializationError.class) public void overloaded() throws InitializationError {
		new BlockJUnit4ClassRunner(Confused.class);
	}
	
	public static class ConstructorParameter {
		public ConstructorParameter(Object something) {
		}
		
		@Test public void a() {
		}
	}
	
	@Test(expected=InitializationError.class) public void constructorParameter() throws InitializationError {
		new BlockJUnit4ClassRunner(ConstructorParameter.class);
	}
	
	public static class OnlyTestIsIgnored {
		@Ignore @Test public void ignored() {}
	}
	
	@Test public void onlyIgnoredMethodsIsStillFineTestClass() {
		Result result= JUnitCore.runClasses(OnlyTestIsIgnored.class);
		assertEquals(0, result.getFailureCount());
		assertEquals(1, result.getIgnoreCount());
	}
}
