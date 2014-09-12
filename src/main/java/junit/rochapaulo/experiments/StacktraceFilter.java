package junit.rochapaulo.experiments;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class StacktraceFilter {

    private static final List<String> supressedPackages = asList(
            "org.eclipse.jdt.internal.junit.runner",
            "org.eclipse.jdt.internal.junit.ui",
            "org.eclipse.jdt.internal.junit4.runner",
            "org.junit",
            "sun.reflect",

            "java.lang.reflect.Method",
            "junit.framework.Assert",
            "junit.framework.TestCase",
            "junit.framework.TestResult",
            "junit.framework.TestResult$1",
            "junit.framework.TestSuite"
        );
    
    public StringBuffer apply(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        cleanUpTrace(t, pw);
        
        return sw.getBuffer();
    }

    private void cleanUpTrace(Throwable t, PrintWriter writer) {
        writer.println(format("%s %s", t.getClass(), t.getMessage()));
        for (StackTraceElement e : t.getStackTrace()) {
            if (shouldWrite(e)) {
                writer.println(e.toString());
            }
        }
    }
    
    private boolean shouldWrite(StackTraceElement traceElement) {
        for (String pkg : supressedPackages) {
            if (traceElement.getClassName().startsWith(pkg)) {
                return false;
            }
        }
        return true;
    }
 
}
