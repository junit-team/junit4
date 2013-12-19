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
 * annotated {@link Rule}s on a class, they will be applied in order of fields first, then methods.
 * However, if there are multiple fields (or methods) they will be applied in an order
 * that depends on your JVM's implementation of the reflection API, which is
 * undefined, in general. Rules defined by fields will always be applied
 * before Rules defined by methods. You can use a {@link org.junit.rules.RuleChain} if you want
 * to have control over the order in which the Rules are applied.
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
 * @since 4.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Rule {

}