package org.junit;

/**
 * This interface is used to identify the scope 
 * of what is being tested by a Test Class (class with @Test annotated methods),
 * The scope of a test could be one of the following;
 * 
 * A Java Class (getScope would return the class name)
 * A Java Package (getScope would return the package name)
 * A Functional Test for a Use Case 
 *      getScope would return 'functional:useCaseName#useCaseCondition')
 *      ie functional:UserAuthenticate#badUserId
 *      functional:UserAuthenticate#badPassword
 *      functional:UserAuthenticate#success
 * 
 * This way external tools/users could provide
 * a more accurate regression of what is getting tested.
 * In example we have a class;
 * public class Foo() {
 *  public String bar() {
 *      return "barnone";
 *  }
 * }
 * 
 * and a test class which is suppose to test the class Foo;
 * public void FooTests() implements TestClassScope {
 *     public  String getScope() {
 *      return Foo.class.getName();
 *     }
 *     
 *     @Test 
 *     public void testBar() {
 *          Foo foo = new Foo();
 *          assertEquals("barnone", foo.bar());
 *     }
 * }
 * 
 * So a external tool like EclEmma can now not only provide
 * what got covered (executed) by the test, but how many assertions
 * occured on those covered lines.  So instead of the current coverage report;
 * 
 * Element, Coverage, Covered Instructions, Missed Instructions, Total Instructions
 * Foo, 100%, 1, 0, 1
 * 
 * We could have something like (Tested would be the assertions/instructions with a max of 100%);
 * Element, Tested, Coverage, Covered Instructions, Missed Instructions, Total Instructions, Total Assertions
 * Foo, 100%, 100%, 1, 0, 1, 1
 * 
 * Implementations of this interface are assumed to be TestClasses 
 * (ie classes with @Test annotations).
 * @author scott
 *
 */
public interface TestClassScope {
    /**
     * return a java class name or a 
     * java package name which corresponds to what
     * this test class is testing.
     * @return
     */
    public String getScope();
}
