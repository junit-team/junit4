package org.junit.tests.experimental;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.tests.experimental.results.PrintableResultTest;
import org.junit.tests.experimental.results.ResultMatchersTest;
import org.junit.tests.experimental.theories.ParameterSignatureTest;
import org.junit.tests.experimental.theories.ParameterizedAssertionErrorTest;
import org.junit.tests.experimental.theories.extendingwithstubs.StubbedTheoriesTest;
import org.junit.tests.experimental.theories.runner.WhenNoParametersMatch;
import org.junit.tests.experimental.theories.runner.WithDataPointFields;
import org.junit.tests.experimental.theories.runner.WithDataPointMethod;
import org.junit.tests.experimental.theories.runner.WithExtendedParameterSources;

@RunWith(Suite.class)
@SuiteClasses( { ParameterizedAssertionErrorTest.class,
		WithDataPointFields.class, PrintableResultTest.class,
		ResultMatchersTest.class, WithDataPointMethod.class,
		ParameterSignatureTest.class, WhenNoParametersMatch.class,
		WithExtendedParameterSources.class, StubbedTheoriesTest.class })
public class ExperimentalTests {

}
