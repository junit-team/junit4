package org.junit.tests.running.globalrules;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Tests {@link BlockJUnit4ClassRunner#getGlobalTestRules()}
 */
public class BlockJunit4ClassRunnerGlobalRulesTest {

    @BeforeClass
    public static void setUp() {

        System.setProperty("junit.globalrules.somerule.executed", "false");
        System.setProperty("junit.global.rules", "org.junit.tests.running.globalrules.SomeRule");
    }

    @Test
    public void testGlobalRuleExectuted() {

        assertEquals(System.getProperty("junit.globalrules.somerule.executed"), "true");
    }
}