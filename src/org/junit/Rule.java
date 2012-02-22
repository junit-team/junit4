package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates fields that contain rules. Such a field must be public, not
 * static, and a subtype of {@link org.junit.rules.TestRule}.  
 * The {@link org.junit.runners.model.Statement} passed 
 * to the {@link org.junit.rules.TestRule} will run any {@link Before} methods, 
 * then the {@link Test} method, and finally any {@link After} methods,
 * throwing an exception if any of these fail.  If there are multiple
 * annotated {@link Rule}s on a class, they will be applied in an order
 * that depends on your JVM's implementation of the reflection API, which is
 * undefined, in general.
 *
 * For example, here is a test class that creates a temporary folder before
 * each test method, and deletes it after each:
 *
 * <pre>
 * public static class HasTempFolder {
 * 	&#064;Rule
 * 	public TemporaryFolder folder= new TemporaryFolder();
 * 
 * 	&#064;Test
 * 	public void testUsingTempFolder() throws IOException {
 * 		File createdFile= folder.newFile(&quot;myfile.txt&quot;);
 * 		File createdFolder= folder.newFolder(&quot;subfolder&quot;);
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * For more information and more examples, see 
 * {@link org.junit.rules.TestRule}. 
 *
 * Note: for backwards compatibility, this annotation may also mark
 * fields of type {@link org.junit.rules.MethodRule}, which will be honored.  However,
 * this is a deprecated interface and feature.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Rule {

}