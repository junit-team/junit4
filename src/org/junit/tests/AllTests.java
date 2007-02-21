package org.junit.tests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// TODO (Feb 21, 2007 10:05:41 AM):  organize these tests

@RunWith(Suite.class)
@SuiteClasses({
	ListenerTest.class,
	FailedConstructionTest.class,
	// TODO: What did CVS do with this?  CustomRunnerTest.class,
	TestDescriptionTest.class,
	SuiteDescriptionTest.class,
	AllTestsTest.class,
	AnnotationTest.class,
	AssertionTest.class,
	CommandLineTest.class,
	ExpectedTest.class,
	ForwardCompatibilityTest.class,
	OldTests.class,
	ParameterizedTestTest.class,
	PreJUnit4TestCaseRunnerTest.class,
	RunWithTest.class,
	RunnerTest.class,
	SuiteTest.class,
	TestListenerTest.class,
	TestMethodTest.class,
	TextListenerTest.class,
	TimeoutTest.class,
	EnclosedTest.class,
	ParameterizedTestMethodTest.class,
	InitializationErrorForwardCompatibilityTest.class,
	SingleMethodTest.class,
	ValidationTest.class,
	UserStopTest.class,
	SortableTest.class,
	OldTestClassRunnerTest.class,
	JUnitCoreTest.class,
	InaccessibleBaseClassTest.class,
	SuiteMethodTest.class,
	TestClassMethodsRunnerTest.class,
	IgnoreClassTest.class,
	OldTestClassAdaptingListenerTest.class
})
public class AllTests {
	public static Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}
}
