package org.junit.experimental.runners;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.Suite;
import org.junit.runners.model.RunnerBuilder;

/**
 * If you put tests in inner classes, Ant, for example, won't find them. By running the outer class
 * with Enclosed, the tests in the inner classes will be run. You might put tests in inner classes
 * to group them for convenience or to share constants. Abstract inner classes are ignored.
 * <p>
 * So, for example:
 * <pre>
 * &#064;RunWith(Enclosed.class)
 * public class ListTests {
 *     ...useful shared stuff...
 *     public static class OneKindOfListTest {...}
 *     public static class AnotherKind {...}
 *     abstract public static class Ignored {...}
 * }
 * </pre>
 */
public class Enclosed extends Suite {
    /**
     * Only called reflectively. Do not use programmatically.
     */
    public Enclosed(Class<?> klass, RunnerBuilder builder) throws Throwable {
        super(builder, klass, filterAbstractClasses(klass.getClasses()));
    }
    
    private static Class<?>[] filterAbstractClasses(final Class<?>[] classes) {     
        final List<Class<?>> filteredList= new ArrayList<Class<?>>(classes.length);

        for (final Class<?> clazz : classes) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                filteredList.add(clazz);
            }
        }
        
        return filteredList.toArray(new Class<?>[filteredList.size()]);
    }   
}
