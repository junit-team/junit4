package org.junit.experimental.test;

import org.junit.experimental.test.results.PrintableResultTest;
import org.junit.experimental.test.results.ResultMatchersTest;
import org.junit.experimental.test.theories.DataPointMethodTest;
import org.junit.experimental.test.theories.ParameterizedAssertionErrorTest;
import org.junit.experimental.test.theories.TheoriesTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ParameterizedAssertionErrorTest.class,
		TheoriesTest.class, PrintableResultTest.class,
		ResultMatchersTest.class, DataPointMethodTest.class })
public class ExperimentalTests {

}
