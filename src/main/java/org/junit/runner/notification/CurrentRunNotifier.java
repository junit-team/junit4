package org.junit.runner.notification;

/**
 * note I couldn't see a way to pass around the RunNotifier
 * everywhere through all the reflection
 * so I decided to use a singleton 
 * 
 * @author scott
 *
 */
public class CurrentRunNotifier {
 // not using JUnitCore.class.getName to obvoid a circular dependancy
    public static final String JUNIT_CORE = "org.junit.runner.JUnitCore";
    public static final String ONLY_JUNIT_CORE_CAN_CREATE = "CurrentRunNotifier can only be reassigned by " + JUNIT_CORE;
    /**
     * create a default instance incase someone
     * calls Assert.equals directly outside of a Test method
     * (so it doesn't go through the JUnitCore run method).
     * 
     */
    private static RunNotifier NOTIFIER = new RunNotifier();

    public CurrentRunNotifier() {
        Exception x = new Exception();
        x.fillInStackTrace();
        StackTraceElement[] traceElements = x.getStackTrace();
        StackTraceElement e0 =traceElements[1];
        String clazzName = e0.getClassName();
        if (!JUNIT_CORE.equals(clazzName)) {
            throw new IllegalStateException(ONLY_JUNIT_CORE_CAN_CREATE);
        }
        
        synchronized (this) {
            NOTIFIER = new RunNotifier();
        }
    }
    
    public static final RunNotifier getNotifier() {
        return NOTIFIER;
    }
}
