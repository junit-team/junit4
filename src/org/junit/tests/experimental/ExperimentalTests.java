package org.junit.tests.experimental;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.tests.experimental.results.PrintableResultTest;
import org.junit.tests.experimental.results.ResultMatchersTest;
import org.junit.tests.experimental.theories.DataPointMethodTest;
import org.junit.tests.experimental.theories.ParameterSignatureTest;
import org.junit.tests.experimental.theories.ParameterizedAssertionErrorTest;
import org.junit.tests.experimental.theories.TheoriesTest;

@RunWith(Suite.class)
@SuiteClasses( { ParameterizedAssertionErrorTest.class,
		TheoriesTest.class, PrintableResultTest.class,
		ResultMatchersTest.class, DataPointMethodTest.class, ParameterSignatureTest.class })
public class ExperimentalTests {

}
