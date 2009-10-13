package org.junit.tests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.tests.assertion.AssertionTest;
import org.junit.tests.assertion.BothTest;
import org.junit.tests.assertion.EachTest;
import org.junit.tests.deprecated.JUnit4ClassRunnerTest;
import org.junit.tests.description.AnnotatedDescriptionTest;
import org.junit.tests.description.SuiteDescriptionTest;
import org.junit.tests.description.TestDescriptionTest;
import org.junit.tests.experimental.AssumptionTest;
import org.junit.tests.experimental.AssumptionViolatedExceptionTest;
import org.junit.tests.experimental.ExperimentalTests;
import org.junit.tests.experimental.MatcherTest;
import org.junit.tests.experimental.categories.CategoryTest;
import org.junit.tests.experimental.max.JUnit38SortingTest;
import org.junit.tests.experimental.max.MaxStarterTest;
import org.junit.tests.experimental.parallel.ParallelClassTest;
import org.junit.tests.experimental.parallel.ParallelMethodTest;
import org.junit.tests.experimental.rules.ExpectedExceptionRuleTest;
import org.junit.tests.experimental.rules.ExternalResourceRuleTest;
import org.junit.tests.experimental.rules.RulesTest;
import org.junit.tests.experimental.rules.NameRulesTest;
import org.junit.tests.experimental.rules.TempFolderRuleTest;
import org.junit.tests.experimental.rules.TimeoutRuleTest;
import org.junit.tests.experimental.rules.VerifierRuleTest;
import org.junit.tests.experimental.theories.AllMembersSupplierTest;
import org.junit.tests.experimental.theories.runner.TheoriesPerformanceTest;
import org.junit.tests.junit3compatibility.AllTestsTest;
import org.junit.tests.junit3compatibility.ClassRequestTest;
import org.junit.tests.junit3compatibility.ForwardCompatibilityTest;
import org.junit.tests.junit3compatibility.InitializationErrorForwardCompatibilityTest;
import org.junit.tests.junit3compatibility.JUnit38ClassRunnerTest;
import org.junit.tests.junit3compatibility.OldTestClassAdaptingListenerTest;
import org.junit.tests.junit3compatibility.OldTests;
import org.junit.tests.junit3compatibility.SuiteMethodTest;
import org.junit.tests.listening.ListenerTest;
import org.junit.tests.listening.RunnerTest;
import org.junit.tests.listening.TestListenerTest;
import org.junit.tests.listening.TextListenerTest;
import org.junit.tests.listening.UserStopTest;
import org.junit.tests.manipulation.FilterableTest;
import org.junit.tests.manipulation.SingleMethodTest;
import org.junit.tests.manipulation.SortableTest;
import org.junit.tests.running.classes.EnclosedTest;
import org.junit.tests.running.classes.IgnoreClassTest;
import org.junit.tests.running.classes.ParameterizedTestTest;
import org.junit.tests.running.classes.RunWithTest;
import org.junit.tests.running.classes.SuiteTest;
import org.junit.tests.running.classes.TestClassTest;
import org.junit.tests.running.classes.UseSuiteAsASuperclassTest;
import org.junit.tests.running.core.CommandLineTest;
import org.junit.tests.running.core.JUnitCoreReturnsCorrectExitCodeTest;
import org.junit.tests.running.core.SystemExitTest;
import org.junit.tests.running.methods.AnnotationTest;
import org.junit.tests.running.methods.ExpectedTest;
import org.junit.tests.running.methods.InheritedTestTest;
import org.junit.tests.running.methods.ParameterizedTestMethodTest;
import org.junit.tests.running.methods.TestMethodTest;
import org.junit.tests.running.methods.TimeoutTest;
import org.junit.tests.validation.BadlyFormedClassesTest;
import org.junit.tests.validation.FailedConstructionTest;
import org.junit.tests.validation.InaccessibleBaseClassTest;
import org.junit.tests.validation.ValidationTest;

// These test files need to be cleaned.  See
// https://sourceforge.net/pm/task.php?func=detailtask&project_task_id=136507&group_id=15278&group_project_id=51407

@SuppressWarnings("deprecation")
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
	JUnit38ClassRunnerTest.class,
	SystemExitTest.class,
	JUnitCoreReturnsCorrectExitCodeTest.class,
	InaccessibleBaseClassTest.class,
	SuiteMethodTest.class,
	BadlyFormedClassesTest.class,
	IgnoreClassTest.class,
	OldTestClassAdaptingListenerTest.class,
	AnnotatedDescriptionTest.class,
	BothTest.class,
	AssumptionViolatedExceptionTest.class,
	EachTest.class,
	ExperimentalTests.class,
	InheritedTestTest.class,
	TestClassTest.class,
	AllMembersSupplierTest.class,
	MatcherTest.class,
	ObjectContractTest.class,
	TheoriesPerformanceTest.class,
	JUnit4ClassRunnerTest.class,
	UseSuiteAsASuperclassTest.class,
	FilterableTest.class,
	MaxStarterTest.class,
	JUnit38SortingTest.class,
	RulesTest.class,
	TimeoutRuleTest.class,
	ParallelClassTest.class,
	ParallelMethodTest.class,
	ParentRunnerTest.class,
	NameRulesTest.class,
	ExpectedExceptionRuleTest.class,
	TempFolderRuleTest.class,
	ExternalResourceRuleTest.class,
	VerifierRuleTest.class,
	CategoryTest.class
})
public class AllTests {
	public static Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}
}
