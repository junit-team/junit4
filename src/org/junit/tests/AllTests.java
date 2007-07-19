package org.junit.tests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// These test files need to be cleaned.  See
// https://sourceforge.net/pm/task.php?func=detailtask&project_task_id=136507&group_id=15278&group_project_id=51407

@RunWith(Suite.class)
@SuiteClasses({
	AssumptionTest.class,
	ClassRequestTest.class,
	ListenerTest.class,
	FailedConstructionTest.class,
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
	OldTestClassAdaptingListenerTest.class,
	AnnotatedDescriptionTest.class,
	BothTest.class,
	AssumptionViolatedExceptionTest.class,
	EachTest.class
})
public class AllTests {
	public static Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}
}
