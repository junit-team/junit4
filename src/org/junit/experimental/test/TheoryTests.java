package org.junit.experimental.test;

import org.junit.experimental.test.assertion.AssumptionViolatedExceptionTest;
import org.junit.experimental.test.imposterization.AssumePassingTest;
import org.junit.experimental.test.imposterization.PopperImposterizerTest;
import org.junit.experimental.test.imposterization.ThrownMatcherTest;
import org.junit.experimental.test.javamodel.ConcreteFunctionTest;
import org.junit.experimental.test.javamodel.FunctionTest;
import org.junit.experimental.test.matchers.EachTest;
import org.junit.experimental.test.matchers.MatcherCharacterization;
import org.junit.experimental.test.results.PrintableResultTest;
import org.junit.experimental.test.results.ResultMatchersTest;
import org.junit.experimental.test.runner.DataPointMethodTest;
import org.junit.experimental.test.runner.ParameterizedAssertionErrorTest;
import org.junit.experimental.test.runner.TheoriesTest;
import org.junit.experimental.test.runner.TheoryContainerReferenceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { AssumptionViolatedExceptionTest.class,
		ConcreteFunctionTest.class, EachTest.class,
		MatcherCharacterization.class, ParameterizedAssertionErrorTest.class,
		TheoriesTest.class, TheoryContainerReferenceTest.class,
		FunctionTest.class, PrintableResultTest.class,
		ResultMatchersTest.class, DataPointMethodTest.class,
		AssumePassingTest.class, PopperImposterizerTest.class,
		ThrownMatcherTest.class })
public class TheoryTests {

}
