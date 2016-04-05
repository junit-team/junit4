package org.junit.samples;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.samples.money.MoneyTest;

@RunWith(Suite.class)
@SuiteClasses({
        ListTest.class,
        MoneyTest.class
})
public class AllSamplesTests {
}
