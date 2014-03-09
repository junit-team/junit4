package org.junit.runner.stats;

import org.junit.runner.notification.Failure;
/**
 * statistics about a particular test method 
 * (ie a method annotated with @Test)
 * 
 * @author scott
 *
 */
public interface I_TestStats {
    /**
     * The length of the test methods run time
     * in milliseconds
     * @return
     */
    public long getRunTime();
    /**
     * The timestamp when this test started, based on the current computers clock
     * @return
     */
    public long getStartTime() ;
    /**
     * the number of assertions (ie assertEquals, assertTrue, assertThat exc)
     * which were called by this test method, or by code that this
     * test method delegated to.
     * @return
     */
    public int getAssertCount() ;
    /**
     * the name of the test method that was run (ie annotated with @Test),
     * note this needs to allow null, in case someone calls
     * Assert methods directly outside of a test method scope
     */
    public String getMethodName() ;
    /**
     * The class which contains test methods (ie annotated with @Test
     * or extends TestCase from the older JUnit 3 api)
     * Assert methods directly outside of a test method scope
     */
    public Class<?> getTestClass();
    /**
     * if this test method passed
     * @return
     */
    public boolean isPass();
    /**
     * the failure if this test method did not pass, otherwise null.
     * @return
     */
    public Failure getFailure();
    
    /**
     * if the TestClass implements TestClassScope
     * pass the scope along, otherwise empty string
     * @return
     */
    public String getScope();
}
