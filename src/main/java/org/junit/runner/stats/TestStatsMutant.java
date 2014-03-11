package org.junit.runner.stats;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.runner.notification.Failure;

/**
 * A mutable implementation of I_TestStats
 * for usage which the test method is running
 * @see I_TestStats
 * 
 * @author scott
 *
 */
public class TestStatsMutant implements I_TestStats{
    public static final String A_METHOD_NAME_IS_REQUIRED = "A MethodName is required.";
    public static final String A_TEST_CLASS_IS_REQUIRED = "A TestClass is required.";
    /**
     * @see I_TestStats#getRunTime()
     */
    private final AtomicLong runTime = new AtomicLong();
    /**
     * @see I_TestStats#getStartTime()
     */
    private final AtomicLong startTime = new AtomicLong();
    /**
     * @see I_TestStats#getAssertCount()
     */
    private final AtomicInteger assertCount = new AtomicInteger();
    /**
     * if the passed vairable has been set yet
     * either by setFailure(Failure p)
     * or by setPass(boolean p)
     */
    private final AtomicBoolean passedSet = new AtomicBoolean(false);
    /**
     * @see I_TestStats#isPass()
     */
    private final AtomicBoolean passed = new AtomicBoolean();
    /**
     * @see I_TestStats#getMethodName()
     */
    private final String methodName;
   /**
    * @see I_TestStats#getTestClassName()
    */
    private String testClassName;
    /**
     * @see I_TestStats#getFailure()
     */
    private Failure failure;
    /**
     * @see I_TestStats#getScope();
     */
     private String scope;
     
    public TestStatsMutant(String pTestClassName, String pMethodName, long pStartTime) {
        testClassName = pTestClassName;
        methodName = pMethodName;
        startTime.set(pStartTime);
        scope = "";
    }
    
    public TestStatsMutant(I_TestStats other) {
        testClassName = other.getTestClassName();
        methodName = other.getMethodName();
        runTime.set(other.getRunTime());
        startTime.set(other.getStartTime());
        assertCount.set(other.getAssertCount());
        passed.set(other.isPass());
        failure = other.getFailure();
        scope = other.getScope();
    }
    
    public long getRunTime() {
        return runTime.longValue();
    }
    public long getStartTime() {
        return startTime.longValue();
    }
    public int getAssertCount() {
        return assertCount.intValue();
    }
    public String getMethodName() {
        return methodName;
    }
    public String getTestClassName() {
        return testClassName;
    }
    
    public void finishTest() {
        long end = System.currentTimeMillis();
        runTime.set(end - startTime.longValue());
    }
    
    public void incrementAssertCount() {
       assertCount.addAndGet(1);
    }
    
    public void setPass(boolean p) {
        if (!passedSet.get()) {
            passedSet.set(true);
            passed.set(p);
        }
        
    }
    
    public boolean isPass() {
        return passed.get();
    }

    public Failure getFailure() {
        return failure;
    }

    public void setFailure(Failure p) {
        setPass(false);
        failure = p;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String p) {
        if (p != null) {
            scope = p;
        } else {
            scope = "";
        }
    }
}
