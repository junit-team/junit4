package junit.runner.stackfilter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Packages implements Iterable<String> {
    
    private Set<String> packageSet = new HashSet<String>();

    public Packages(String[] pkgs) {
        add(pkgs);
    }
    
    public void add(String[] pkgs) {
        for (String pkg : pkgs) {
            packageSet.add(pkg);
        }
    }
    
    public static Packages defaultPackages() {
        return new Packages(new String[]{
            "junit.framework.TestCase",
            "junit.framework.TestResult",
            "junit.framework.TestSuite",
            "junit.swingui.TestRunner",
            "junit.awtui.TestRunner",
            "junit.textui.TestRunner",
            "sun.reflect",
            "java.lang.reflect.Method.invoke"});
    }

    public Iterator<String> iterator() {
        return packageSet.iterator();
    }
    
}

