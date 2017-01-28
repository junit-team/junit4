package org.junit.fixtures;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates static fields that reference test fixtures or methods that return them. A field must be public,
 * static and implement {@link org.junit.fixtures.TestFixture}. A method must be public static, and return
 * a subtype of {@link org.junit.rules.TestRule}. Fields and methods of this type are called Class Fixtures.
 *
 * <p>Class fixtures will be initialized before all the test methods in a the class or fixture
 * where the fixture is installed, , and will have their teardowns and postconditions called after
 * all the test method.s The fixture initiation occurs before any {@link org.junit.BeforeClass}
 * methods are called, and the postconditions and teardowns are called after all 
 * {@link org.junit.AfterClass} methods.
 * 
 * If there are multiple members annotated with {@link TestFixture} in a class, they will be
 * applied in an order that depends on your JVM's implementation of the reflection API, which is
 * undefined, in general. However, fixtures defined by fields will always be applied before fixtures
 * defined by methods.
 *
 * <p> For example, here is a test suite that connects to a server once before
 * all the test classes run, and disconnects after they are finished:
 * <pre>
 * &#064;RunWith(Suite.class)
 * &#064;SuiteClasses({A.class, B.class, C.class})
 * public class UsesServer {
 *     public static final Server myServer = new Server();
 *
 *     &#064;ClassFixture
 *     public static AbstractTestFixture runServer = new AbstractTestFixture() {
 *       &#064;Override
 *       protected void beforeTest() throws Exception {
 *          myServer.connect();
 *      }
 *
 *      &#064;Override
 *      protected void afterTest() throws Exception {
 *          myServer.disconnect();
 *      }
 *   };
 * }
 * </pre>
 * <p>
 * and the same using a method
 * <pre>
 * &#064;RunWith(Suite.class)
 * &#064;SuiteClasses({A.class, B.class, C.class})
 * public class UsesServer {
 *     public static final Server myServer = new Server();
 *
 *     &#064;ClassFixture
 *     public static TestFixture runServer() {
 *         return new AbstractTestFixture() {
 *             &#064;Override
 *             protected void beforeTest() throws Exception {
 *                 myServer.connect();
 *             }
 *
 *             &#064;Override
 *             protected void afterTest() throws Exception {
 *                 myServer.disconnect();
 *             }
 *         };
 *     }
 * }
 * </pre>
 *
 * @since 4.13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ClassFixture {
}
