
# JUnit 4.6

Brought to you by [Kent Beck][1], Erich Gamma, and [David Saff][2].

FAQ edited by [Mike Clark][3]. Web mastering by Erik Meade.

(see also [JUnit.org][4]) 
* * *

  
6 April 2009

JUnit is a simple framework to write repeatable tests. It is an instance of the xUnit architecture for unit testing frameworks.

*   [Summary of Changes](#summary_of_changes)
*   [Contents](#contents)
*   [Installation](#installation)
*   [Getting Started](#getting_started)
*   [Documentation](#documentation1)
*   [Known Defects](#known_defects)

<a id="summary_of_changes"></a>
## Summary of Changes in version 4.6

### Max

JUnit now includes a new experimental Core, `MaxCore`. `MaxCore` remembers the results of previous test runs in order to run new tests out of order. `MaxCore` prefers new tests to old tests, fast tests to slow tests, and recently failing tests to tests that last failed long ago. There's currently not a standard UI for running `MaxCore` included in JUnit, but there is a UI included in the JUnit Max Eclipse plug-in at:

[http://www.junitmax.com/junitmax/subscribe.html](http://www.junitmax.com/junitmax/subscribe.html)

Example:

    public static class TwoUnEqualTests {
        @Test
        public void slow() throws InterruptedException {
            Thread.sleep(100);
            fail();
        }
    
        @Test
        public void fast() {
            fail();
        }
    }
    
    @Test
    public void rememberOldRuns() {
        File maxFile = new File("history.max");
        MaxCore firstMax = MaxCore.storedLocally(maxFile);
        firstMax.run(TwoUnEqualTests.class);
    
        MaxCore useHistory= MaxCore.storedLocally(maxFile);
        List failures= useHistory.run(TwoUnEqualTests.class)
                .getFailures();
        assertEquals("fast", failures.get(0).getDescription().getMethodName());
        assertEquals("slow", failures.get(1).getDescription().getMethodName());
    }
    

### Test scheduling strategies

`JUnitCore` now includes an experimental method that allows you to specify a model of the `Computer` that runs your tests. Currently, the only built-in Computers are the default, serial runner, and two runners provided in the `ParallelRunner` class: `ParallelRunner.classes()`, which runs classes in parallel, and `ParallelRunner.methods()`, which runs classes and methods in parallel.

This feature is currently less stable than MaxCore, and may be merged with MaxCore in some way in the future.

Example:

    public static class Example {
        @Test public void one() throws InterruptedException {
            Thread.sleep(1000);
        }
        @Test public void two() throws InterruptedException {
            Thread.sleep(1000);
        }
    }
    
    @Test public void testsRunInParallel() {
        long start= System.currentTimeMillis();
        Result result= JUnitCore.runClasses(ParallelComputer.methods(),
                Example.class);
        assertTrue(result.wasSuccessful());
        long end= System.currentTimeMillis();
        assertThat(end - start, betweenInclusive(1000, 1500));
    }
    

### Comparing double arrays

Arrays of doubles can be compared, using a delta allowance for equality:

    @Test
    public void doubleArraysAreEqual() {
        assertArrayEquals(new double[] {1.0, 2.0}, new double[] {1.0, 2.0}, 0.01);
    }
    

### `Filter.matchDescription` API

Since 4.0, it has been possible to run a single method using the `Request.method` API. In 4.6, the filter that implements this is exposed as `Filter.matchDescription`.

### Documentation

*   A couple classes and packages that once had empty javadoc have been doc'ed.

*   Added how to run JUnit from the command line to the cookbook.

*   junit-4.x.zip now contains build.xml

### Bug fixes

*   Fixed overly permissive @DataPoint processing (2191102)
*   Fixed bug in test counting after an ignored method (2106324)

## Summary of Changes in version 4.5

### Installation

*   We are releasing `junit-4.6.jar`, which contains all the classes necessary to run JUnit, and `junit-dep-4.6.jar`, which leaves out hamcrest classes, for developers who already use hamcrest outside of JUnit.

### Basic JUnit operation

*   JUnitCore now more often exits with the correct exit code (0 for success, 1 for failure)

*   Badly formed test classes (exceptions in constructors, classes without tests, multiple constructors, Suite without @SuiteClasses) produce more helpful error messages

*   Test classes whose only test methods are inherited from superclasses now run.

*   Optimization to annotation processing can cut JUnit overhead by more than half on large test classes, especially when using Theories. [Bug 1796847]

*   A failing assumption in a constructor ignores the class

*   Correct results when comparing the string "null" with potentially null values. [Bug 1857283]

*   Annotating a class with `@RunWith(JUnit4.class)` will always invoke the default JUnit 4 runner in the current version of JUnit. This default changed from `JUnit4ClassRunner` in 4.4 to `BlockJUnit4ClassRunner` in 4.5 (see below), and may change again.

### Extension

*   `BlockJUnit4Runner` is a new implementation of the standard JUnit 4 test class functionality. In contrast to `JUnit4ClassRunner` (the old implementation):
    
    *   `BlockJUnit4Runner` has a much simpler implementation based on Statements, allowing new operations to be inserted into the appropriate point in the execution flow.
    
    *   `BlockJUnit4Runner` is published, and extension and reuse are encouraged, whereas `JUnit4ClassRunner` was in an internal package, and is now deprecated.

*   `ParentRunner` is a base class for runners that iterate over a list of "children", each an object representing a test or suite to run. `ParentRunner` provides filtering, sorting, `@BeforeClass`, `@AfterClass`, and method validation to subclasses.

*   `TestClass` wraps a class to be run, providing efficient, repeated access to all methods with a given annotation.

*   The new `RunnerBuilder` API allows extending the behavior of Suite-like custom runners.

*   `AssumptionViolatedException.toString()` is more informative

### Extra Runners

*   `Parameterized.eachOne()` has been removed

*   New runner `Enclosed` runs all static inner classes of an outer class.

### Theories

*   `@Before` and `@After` methods are run before and after each set of attempted parameters on a Theory, and each set of parameters is run on a new instance of the test class.

*   Exposed API's `ParameterSignature.getType()` and `ParameterSignature.getAnnotations()`

*   An array of data points can be introduced by a field or method marked with the new annotation `@DataPoints`

*   The Theories custom runner has been refactored to make it faster and easier to extend

### Development

*   Source has been split into directories `src/main/java` and `src/test/java`, making it easier to exclude tests from builds, and making JUnit more maven-friendly

*   Test classes in `org.junit.tests` have been organized into subpackages, hopefully making finding tests easier.

*   `ResultMatchers` has more informative descriptions.

*   `TestSystem` allows testing return codes and other system-level interactions.

## Summary of Changes in version 4.4

JUnit is designed to efficiently capture developers' intentions about their code, and quickly check their code matches those intentions. Over the last year, we've been talking about what things developers would like to say about their code that have been difficult in the past, and how we can make them easier.

### assertThat

Two years ago, Joe Walnes built a [new assertion mechanism][5] on top of what was then [JMock 1][6]. The method name was `assertThat`, and the syntax looked like this:

    assertThat(x, is(3));
    assertThat(x, is(not(4)));
    assertThat(responseString, either(containsString("color")).or(containsString("colour")));
    assertThat(myList, hasItem("3"));
    

More generally:

    assertThat([value], [matcher statement]);
    

Advantages of this assertion syntax include:

*   More readable and typeable: this syntax allows you to think in terms of subject, verb, object (assert "x is 3") rathern than `assertEquals`, which uses verb, object, subject (assert "equals 3 x")

*   Combinations: any matcher statement `s` can be negated (`not(s)`), combined (`either(s).or(t)`), mapped to a collection (`each(s)`), or used in custom combinations (`afterFiveSeconds(s)`)

*   Readable failure messages. Compare
    
        assertTrue(responseString.contains("color") || responseString.contains("colour"));
        // ==> failure message: 
        // java.lang.AssertionError:
        
        
        assertThat(responseString, anyOf(containsString("color"), containsString("colour")));
        // ==> failure message:
        // java.lang.AssertionError: 
        // Expected: (a string containing "color" or a string containing "colour")
        //      got: "Please choose a font"
        

*   Custom Matchers. By implementing the `Matcher` interface yourself, you can get all of the above benefits for your own custom assertions.

*   For a more thorough description of these points, see [Joe Walnes's original post][7].:

We have decided to include this API directly in JUnit. It's an extensible and readable syntax, and because it enables new features, like [assumptions](#assumption) and [theories](#theories).

Some notes:

*   The old assert methods are never, ever, going away.   
    Developers may continue using the old `assertEquals`, `assertTrue`, and so on.
*   The second parameter of an `assertThat` statement is a `Matcher`. We include the Matchers we want as static imports, like this:
    
        import static org.hamcrest.CoreMatchers.is;
        
    
    or:
    
        import static org.hamcrest.CoreMatchers.*;
        

*   Manually importing `Matcher` methods can be frustrating. \[Eclipse 3.3\]\[\] includes the ability to define "Favorite" classes to import static methods from, which makes it easier (Search for "Favorites" in the Preferences dialog). We expect that support for static imports will improve in all Java IDEs in the future.

*   To allow compatibility with a wide variety of possible matchers, we have decided to include the classes from hamcrest-core, from the [Hamcrest][8] project. This is the first time that third-party classes have been included in JUnit. 

*   To allow developers to maintain full control of the classpath contents, the JUnit distribution also provides an unbundled junit-dep jar, ie without hamcrest-core classes included. This is intended for situations when using other libraries that also depend on hamcrest-core, to avoid classloading conflicts or issues. Developers using junit-dep should ensure a compatible version of hamcrest-core jar (ie 1.1%2B) is present in the classpath.

*   JUnit currently ships with a few matchers, defined in `org.hamcrest.CoreMatchers` and `org.junit.matchers.JUnitMatchers`.   
    To use many, many more, consider downloading the [full hamcrest package][9].

*   JUnit contains special support for comparing string and array values, giving specific information on how they differ. This is not yet available using the `assertThat` syntax, but we hope to bring the two assert methods into closer alignment in future releases.

<a id="assumption"></a>
### assumeThat

 Ideally, the developer writing a test has control of all of the forces that might cause a test to fail. If this isn't immediately possible, making dependencies explicit can often improve a design.   
For example, if a test fails when run in a different locale than the developer intended, it can be fixed by explicitly passing a locale to the domain code.

However, sometimes this is not desirable or possible.   
It's good to be able to run a test against the code as it is currently written, implicit assumptions and all, or to write a test that exposes a known bug. For these situations, JUnit now includes the ability to express "assumptions":

    import static org.junit.Assume.*
    
    @Test public void filenameIncludesUsername() {
       assumeThat(File.separatorChar, is('/'));
       assertThat(new User("optimus").configFileName(), is("configfiles/optimus.cfg"));
    }
    
    @Test public void correctBehaviorWhenFilenameIsNull() {
       assumeTrue(bugFixed("13356"));  // bugFixed is not included in JUnit
       assertThat(parse(null), is(new NullDocument()));
    }
    

With this beta release, a failed assumption will lead to the test being marked as passing, regardless of what the code below the assumption may assert. In the future, this may change, and a failed assumption may lead to the test being ignored: however, third-party runners do not currently allow this option.

We have included `assumeTrue` for convenience, but thanks to the inclusion of Hamcrest, we do not need to create `assumeEquals`, `assumeSame`, and other analogues to the `assert*` methods. All of those functionalities are subsumed in assumeThat, with the appropriate matcher.

A failing assumption in a `@Before` or `@BeforeClass` method will have the same effect as a failing assumption in each `@Test` method of the class.

<a id="theories"></a>
### Theories

 More flexible and expressive assertions, combined with the ability to state assumptions clearly, lead to a new kind of statement of intent, which we call a "Theory". A test captures the intended behavior in one particular scenario. A theory allows a developer to be as precise as desired about the behavior of the code in possibly infinite numbers of possible scenarios. For example:

    @RunWith(Theories.class)
    public class UserTest {
      @DataPoint public static String GOOD_USERNAME = "optimus";
      @DataPoint public static String USERNAME_WITH_SLASH = "optimus/prime";
    
      @Theory public void filenameIncludesUsername(String username) {
        assumeThat(username, not(containsString("/")));
        assertThat(new User(username).configFileName(), containsString(username));
      }
    }
    

This makes it clear that the user's filename should be included in the config file name, only if it doesn't contain a slash. Another test or theory might define what happens when a username does contain a slash.

`UserTest` will attempt to run `filenameIncludesUsername` on every compatible `DataPoint` defined in the class. If any of the assumptions fail, the data point is silently ignored. If all of the assumptions pass, but an assertion fails, the test fails.

The support for Theories has been absorbed from the [Popper project][10], and [more complete documentation][11] can be found there.

Defining general statements in this way can jog the developer's memory about other potential data points and tests, also allows [automated tools][12] to [search][13] for new, unexpected data points that expose bugs.

### Other changes

This release contains other bug fixes and new features. Among them:

*   Annotated descriptions
    
    Runner UIs, Filters, and Sorters operate on Descriptions of test methods and test classes. These Descriptions now include the annotations on the original Java source element, allowing for richer display of test results, and easier development of annotation-based filters.

*   Bug fix (1715326): assertEquals now compares all Numbers using their native implementation of `equals`. This assertion, which passed in 4.3, will now fail:
    
    assertEquals(new Integer(1), new Long(1));
    
    Non-integer Numbers (Floats, Doubles, BigDecimals, etc), which were compared incorrectly in 4.3, are now fixed.

*   `assertEquals(long, long)` and `assertEquals(double, double)` have been re-introduced to the `Assert` class, to take advantage of Java's native widening conversions. Therefore, this still passes:
    
    assertEquals(1, 1L);

*   The default runner for JUnit 4 test classes has been refactored. The old version was named `TestClassRunner`, and the new is named `JUnit4ClassRunner`. Likewise, `OldTestClassRunner` is now `JUnit3ClassRunner`. The new design allows variations in running individual test classes to be expressed with fewer custom classes. For a good example, see the source to `org.junit.experimental.theories.Theories`.

*   The rules for determining which runner is applied by default to a test class have been simplified:
    
    1.  If the class has a `@RunWith` annotation, the annotated runner class is used.
    
    2.  If the class can be run with the JUnit 3 test runner (it subclasses `TestCase`, or contains a `public static Test suite()` method), JUnit38ClassRunner is used.
    
    3.  Otherwise, JUnit4ClassRunner is used.
    
    This default guess can always be overridden by an explicit `@RunWith(JUnit4ClassRunner.class)` or `@RunWith(JUnit38ClassRunner.class)` annotation.
    
    The old class names `TestClassRunner` and `OldTestClassRunner` remain as deprecated.

*   Bug fix (1739095): Filters and Sorters work correctly on test classes that contain a `suite` method like:
    
    public static junit.framework.Test suite() { return new JUnit4TestAdapter(MyTest.class); }

*   Bug fix (1745048): @After methods are now correctly called after a test method times out.


## Summary of Changes in version 4.3.1

*   Bug fix: 4.3 introduced a [bug][14] that caused a NullPointerException when comparing a null reference to a non-null reference in assertEquals. This has been fixed. 

*   Bug fix: The binary jar for 4.3 [accidentally][15] included the tests and sample code, which are now removed for a smaller download, but, as always, available from the full zip.   

## Summary of Changes with version 4.3

*   Changes in array equality. Using assertEquals to compare array contents is now deprecated. In the future, assertEquals will revert to its pre-4.0 meaning of comparing objects based on Java's Object.equals semantics. To compare array contents, use the new, more reliable Assert.assertArrayEquals methods.
* The @Ignore annotation can now be applied to classes, to ignore the entire class, instead of individual methods. 
*   Originally, developers who wanted to use a static suite() method from JUnit 3.x with a JUnit 4.x runner had to annotate the class with @RunWith(AllTests.class). In the common case, this requirement has been removed. However, when such a class is wrapped with a JUnit4TestAdapter (which we believe is rare), the results may not be as expected. 
*   Improved error messages for array comparison("arrays first differed at element \[1\]\[0\]") 
*   Bug fix: Inaccessible base class is caught at test construction time. 
*   Bug fix: Circular suites are caught at test construction time. 
*   Bug fix: Test constructors that throw exceptions are reported correctly. 

*   For committers and extenders
    *   Sources now are in a separate "src" directory (this means a big break in the CVS history) 
    *   Improved documentation in Request, RunWith    

## Summary of Changes with version 4.2
                                                
*   Bug fix: Inaccessible base class is caught at test construction time. 
*   Bug fix: Circular suites are caught at test construction time. 
*   Improved error messages for array comparison("arrays first differed at element \[1\]\[0\]") 
*   Test constructors that throw exceptions are reported correctly.   

## Summary of Changes with version 4.1
                                                                
*   Bug fix: listeners now get a correct test running time, rather than always being told 0 secs. 
*   The @RunWith annotation is now inherited by subclasses: all subclasses of an abstract test class will be run by the same runner. 
*   The build script fails if the JUnit unit tests fail 
*   The faq has been updated 
*   Javadoc has been improved, with more internal links, and package descriptions added (Thanks, Matthias Schmidt!) 
*   An acknowledgements.txt file has been created to credit outside contributions 
*   The Enclosed runner, which runs all of the static inner classes of a given class, has been added to org.junit.runners.   

## Summary of Changes with version 4.0

The architecture of JUnit 4.0 is a substantial departure from that of earlier releases. Instead of tagging test classes by subclassing junit.framework.TestCase and tagging test methods by starting their name with "test", you now tag test methods with the @Test annotation.

<a id="contents"></a>
## Contents of the Release 
`README` - this file

`junit-4.6.jar` - a jar file with the JUnit framework, bundled with the hamcrest-core-1.1 dependency.

`junit-dep-4.6.jar` - a jar file with the JUnit framework, unbundled from any external dependencies. Choosing to use this jar developers will need to also provide in the classpath a compatible version of external dependencies (ie hamcrest-core-1.1%2B)

`junit-4.6-src.jar` - a jar file with the source code of the JUnit framework

`org/junit` - the source code of the basic JUnit annotations and classes

`samples` - sample test cases

`tests` - test cases for JUnit itself

`javadoc` - javadoc generated documentation

`doc` - documentation and articles 

<a id="installation"></a>
## Installation
Below are the installation steps for installing JUnit: 

1.  unzip the junit4.6.zip file
2.  add `junit-4.6.jar` to the CLASSPATH. For example:  set classpath=%classpath%;INSTALL\_DIR\\junit-4.6.jar;INSTALL\_DIR
3.  test the installation by running java org.junit.runner.JUnitCore org.junit.tests.AllTests

**Notice**: that the tests are not contained in the junit-4.6.jar but in the installation directory directly. Therefore make sure that the installation directory is on the class path

**Important**: don't install junit-4.6.jar into the extension directory of your JDK installation. If you do so the test class on the files system will not be found. 

<a id="getting_started"></a>
## Getting Started

To get started with unit testing and JUnit read the article:  [JUnit Cookbook][16].   

This article describes basic test writing using JUnit 4. 
You find additional samples in the org.junit.samples package: 
*   SimpleTest.java - some simple test cases
*   VectorTest.java - test cases for java.util.Vector

<a id="documentation1"></a>
## Documentation

[JUnit Cookbook][16] - A cookbook for implementing tests with JUnit.   
[Javadoc][17] - API documentation generated with javadoc.   
[Frequently asked questions][18] - Some frequently asked questions about using JUnit.   
[License][19] - The terms of the common public license used for JUnit.  

The following documents still describe JUnit 3.8.
 
[Test Infected - Programmers Love Writing Tests][20] - An article demonstrating the development process with JUnit.   
[JUnit - A cooks tour][21] 
* * *

[1]: http://www.threeriversinstitute.org
[2]: http://david.saff.net
[3]: http://www.clarkware.com
[4]: http://www.junit.org
[5]: http://joe.truemesh.com/blog/000511.html
[6]: http://www.jmock.org/download.html
[7]: http://joe.truemesh.com/blog/000511.html
[8]: http://code.google.com/p/hamcrest/
[9]: http://hamcrest.googlecode.com/files/hamcrest-all-1.1.jar
[10]: http://popper.tigris.org
[11]: http://popper.tigris.org/tutorial.html
[12]: http://www.junitfactory.org
[13]: http://shareandenjoy.saff.net/2007/04/popper-and-junitfactory.html
[14]: https://sourceforge.net/tracker/?func=detail&atid=115278&aid=1684562&group_id=15278
[15]: https://sourceforge.net/tracker/?func=detail&atid=115278&aid=1686931&group_id=15278
[16]: doc/cookbook/cookbook.htm
[17]: javadoc/index.html
[18]: doc/faq/faq.htm
[19]: cpl-v10.html
[20]: doc/testinfected/testing.htm
[21]: doc/cookstour/cookstour.htm
