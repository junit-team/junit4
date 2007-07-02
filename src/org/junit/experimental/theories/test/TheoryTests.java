package org.junit.experimental.theories.test;

import org.junit.experimental.theories.test.assertion.AssumptionViolatedExceptionTest;
import org.junit.experimental.theories.test.imposterization.AssumePassingTest;
import org.junit.experimental.theories.test.imposterization.PopperImposterizerTest;
import org.junit.experimental.theories.test.imposterization.ThrownMatcherTest;
import org.junit.experimental.theories.test.javamodel.ConcreteFunctionTest;
import org.junit.experimental.theories.test.javamodel.FunctionTest;
import org.junit.experimental.theories.test.matchers.CamelCaseNameTest;
import org.junit.experimental.theories.test.matchers.ClassNamedMatcherTest;
import org.junit.experimental.theories.test.matchers.EachTest;
import org.junit.experimental.theories.test.matchers.MatcherCharacterization;
import org.junit.experimental.theories.test.matchers.MethodNamedMatcherTest;
import org.junit.experimental.theories.test.matchers.StackTraceTest;
import org.junit.experimental.theories.test.results.PrintableResultTest;
import org.junit.experimental.theories.test.results.ResultMatchersTest;
import org.junit.experimental.theories.test.runner.DataPointMethodTest;
import org.junit.experimental.theories.test.runner.ParameterizedAssertionErrorTest;
import org.junit.experimental.theories.test.runner.TheoriesTest;
import org.junit.experimental.theories.test.runner.TheoryContainerReferenceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { AssumptionViolatedExceptionTest.class,
		ConcreteFunctionTest.class, CamelCaseNameTest.class,
		ClassNamedMatcherTest.class, EachTest.class,
		MatcherCharacterization.class, MethodNamedMatcherTest.class,
		StackTraceTest.class, ParameterizedAssertionErrorTest.class,
		TheoriesTest.class, TheoryContainerReferenceTest.class,
		FunctionTest.class, PrintableResultTest.class,
		ResultMatchersTest.class, DataPointMethodTest.class,
		AssumePassingTest.class, PopperImposterizerTest.class,
		ThrownMatcherTest.class })
public class TheoryTests {

}
