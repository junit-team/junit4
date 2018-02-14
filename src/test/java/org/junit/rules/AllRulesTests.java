package org.junit.rules;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        BlockJUnit4ClassRunnerOverrideTest.class,
        ClassRulesTest.class,
        DisableOnDebugTest.class,
        ErrorCollectorTest.class,
        ExpectedExceptionTest.class,
        ExternalResourceRuleTest.class,
        MethodRulesTest.class,
        NameRulesTest.class,
        RuleChainTest.class,
        RuleMemberValidatorTest.class,
        StopwatchTest.class,
        TempFolderRuleTest.class,
        TemporaryFolderRuleAssuredDeletionTest.class,
        TemporaryFolderUsageTest.class,
        TestRuleTest.class,
        TestWatcherTest.class,
        TestWatchmanTest.class,
        TestWatchmanTest.class,
        TimeoutRuleTest.class,
        VerifierRuleTest.class
})
public class AllRulesTests {
}
