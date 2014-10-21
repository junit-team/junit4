package junit.runner.stackfilter;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

public class TraceFilter {
    
    private static Packages packages;
    
    private TraceFilter() {
        super();
    } 

    public static String filter(String trace) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        StringReader sr = new StringReader(trace);
        BufferedReader br = new BufferedReader(sr);

        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (!filterLine(line)) {
                    pw.println(line);
                }
            }
        } catch (Exception IOException) {
            return trace;
        }
        
        return sw.toString();
    }
    
    private static boolean filterLine(String line) {
        for (String pkg : packages) {
            if (line.indexOf(pkg) > 0) {
                return true;
            }
        }
        return false;
    }
    
    public static void setPackages(Packages pkgs) {
        packages = pkgs;
    }
    
}
