package org.junit.fixtures;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AnnotaWtes fields that reference test fixtures or methods that return a test fixture. A field must be
 * public, non-static and a subtype of {@link TestFixture}. A method must be public and must return a subtype
 * of {@link TestFixture}. Fields and methods of this type are called Method Fixtures.
 * 
 * <p>Method fixtures will be initialized before each test method in the class, and will
 * have their teardowns and postconditions called after each test method. The fixture
 * initiation occurs before any {@link org.junit.Before} methods are called, and the
 * postconditions and teardowns are called after all {@link org.junit.After} methods.
 * 
 * <p>For example, here is a test class that creates a temporary directory before
 * each test method, and deletes it after each:
 * <pre>
 * public static class HasTempDirectory {
 *     &#064;Fixture
 *     public final TemporaryDirectory tmpDir = new TemporaryDirectory();
 *
 *     &#064;Test
 *     public void testUsingTempFolder() throws IOException {
 *         File createdFile = tmpDir.newFile(&quot;myfile.txt&quot;);
 *         File createdDir = tmpDir.newDirectory(&quot;subDir&quot;);
 *         // ...
 *     }
 * }
 * </pre>
 * <p>
 * And the same using a method.
 * <pre>
 * public static class HasTempDirectory {
 *     private final TemporaryDirectory tmpDir = new TemporaryDirectory();
 *
 *     &#064;Fixture
 *     public TemporaryFolder createTmpDitr() {
 *         return tmpDir;
 *     }
 *
 *     &#064;Test
 *     public void testUsingTempDirectory() throws IOException {
 *         File createdFile = tmpDir.newFile(&quot;myfile.txt&quot;);
 *         File createdDir = tmpDir.newDirectory(&quot;subDir&quot;);
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @since 4.13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Fixture  {
}
