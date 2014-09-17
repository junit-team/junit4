package junit.rochapaulo.experiments;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.junit.Assert;


public class StacktraceFilterTest extends TestCase {
    
    @org.junit.Test public void test() { Assert.assertEquals(Long.valueOf(10), Long.valueOf(50)); }

    public static Test suite() {
        return new TestSuite(StacktraceFilterTest.class);
    }
 
    public static void main(String[] args) throws Exception {
        String[] arguments = new String[] {
                "-c", 
                "junit.rochapaulo.experiments.StacktraceFilterTest",
                "-nostacktrace"
           };
        
        TestRunner runner = new TestRunner();
        runner.start(arguments);
    }
    
}
