package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates fields that reference rules or methods that return a rule. A field must be public, not
 * static, and a subtype of {@link org.junit.rules.TestRule} (preferred) or
 * {@link org.junit.rules.MethodRule}. A method must be public, not static,
 * and must return a subtype of {@link org.junit.rules.TestRule} (preferred) or
 * {@link org.junit.rules.MethodRule}.
 * <p>
 * The {@link org.junit.runners.model.Statement} passed
 * to the {@link org.junit.rules.TestRule} will run any {@link Before} methods,
 * then the {@link Test} method, and finally any {@link After} methods,
 * throwing an exception if any of these fail.  If there are multiple
 * annotated {@link Rule}s on a class, they will be applied in order of methods first, then fields.
 * However, if there are multiple fields (or methods) they will be applied in an order
 * that depends on your JVM's implementation of the reflection API, which is
 * undefined, in general. Rules defined by fields will always be applied
 * after Rules defined by methods, i.e. the Statements returned by the former will
 * be executed around those returned by the latter.
 *
 * <h3>Usage</h3>
 * <p>
 * For example, here is a test class that creates a temporary folder before
 * each test method, and deletes it after each:
 * <pre>
 * public static class HasTempFolder {
 *     &#064;Rule
 *     public TemporaryFolder folder= new TemporaryFolder();
 *
 *     &#064;Test
 *     public void testUsingTempFolder() throws IOException {
 *         File createdFile= folder.newFile(&quot;myfile.txt&quot;);
 *         File createdFolder= folder.newFolder(&quot;subfolder&quot;);
 *         // ...
 *     }
 * }
 * </pre>
 * <p>
 * And the same using a method.
 * <pre>
 * public static class HasTempFolder {
 *     private TemporaryFolder folder= new TemporaryFolder();
 *
 *     &#064;Rule
 *     public TemporaryFolder getFolder() {
 *         return folder;
 *     }
 *
 *     &#064;Test
 *     public void testUsingTempFolder() throws IOException {
 *         File createdFile= folder.newFile(&quot;myfile.txt&quot;);
 *         File createdFolder= folder.newFolder(&quot;subfolder&quot;);
 *         // ...
 *     }
 * }
 * </pre>
 * <p>
 * For more information and more examples, see
 * {@link org.junit.rules.TestRule}.
 *
 * <h3>Ordering</h3>
 * <p>
 * You can use {@link #order()} if you want to have control over the order in
 * which the Rules are applied.
 *
 * <pre>
 * public class ThreeRules {
 *     &#064;Rule(order = 0)
 *     public LoggingRule outer = new LoggingRule("outer rule");
 *
 *     &#064;Rule(order = 1)
 *     public LoggingRule middle = new LoggingRule("middle rule");
 *
 *     &#064;Rule(order = 2)
 *     public LoggingRule inner = new LoggingRule("inner rule");
 *
 *     // ...
 * }
 * </pre>
 *
 * @since 4.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Rule {

    int DEFAULT_ORDER = -1;

    /**
     * Specifies the order in which rules are applied. The rules with a higher value are inner.
     *
     * @since 4.13
     */
    int order() default DEFAULT_ORDER;

}
