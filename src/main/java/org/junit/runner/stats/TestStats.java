package org.junit.runner.stats;

import java.io.Serializable;
import java.math.BigDecimal;

import org.junit.runner.notification.Failure;
/**
 * a Immutable class which implements
 * I_TestStats @see I_TestStats
 */
public class TestStats implements I_TestStats, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * @see I_TestStats#getTestClassName()
     */
    private String testClassName;
    /**
     * @see I_TestStats#getMethodName()
     */
    private String methodName;
    /**
     * @see I_TestStats#getStartTime()
     */
    private long startTime;
    /**
     * @see I_TestStats#getRunTime()
     */
    private long runTime;
    /**
     * @see I_TestStats#isPass()
     */
    private boolean passed;
    /**
     * @see I_TestStats#getFailure()
     */
    private Failure failure;
    /**
     * @see I_TestStats#getAssertCount()
     */
    private int assertionCount;
   /**
    * @see I_TestStats#getScope();
    */
    private String scope;
    
    public TestStats(String pTestClassName, String pMethodName, long pStartTime) {
        testClassName = pTestClassName;
        methodName = pMethodName;
        startTime = pStartTime;
        scope = "";
    }

    public TestStats(I_TestStats stats) {
        testClassName = stats.getTestClassName();
        methodName = stats.getMethodName();
        startTime = stats.getStartTime();
        runTime = stats.getRunTime();
        passed = stats.isPass();
        failure = stats.getFailure();
        assertionCount = stats.getAssertCount();
        scope = stats.getScope();
    }
   
    public long getRunTime() {
        return runTime;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public int getAssertCount() {
        return assertionCount;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public String getTestClassName() {
        return testClassName;
    }

    public boolean isPass() {
        return passed;
    }
    
    public Failure getFailure() {
        return failure;
    }
    
    public String getRuntimeSeconds() {
        BigDecimal rt = new BigDecimal(runTime);
        rt = rt.divide(new BigDecimal(1000));
        return rt.toPlainString();
    }

    public String getScope() {
        return scope;
    }
}
