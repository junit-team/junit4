## Summary of Changes in version 4.5 ##

### Categories ###
Each test method and test class can be annotated as belonging to a _category_:

```java
public static class SomeUITests {
    @Category(UserAvailable.class)
    @Test
    public void askUserToPressAKey() { }
    
    @Test
    public void simulatePressingKey() { }
}
    
@Category(InternetConnected.class)
public static class InternetTests {
    @Test
    public void pingServer() { }
}
```

To run all of the tests in a particular category, you must currently explicitly create a custom request:

```java
new JUnitCore().run(Request.aClass(SomeUITests.class).inCategories(UserAvailable.class));
```
  
This feature will very likely be improved before the final release of JUnit 4.5

### Theories ###

- `@Before` and `@After` methods are run before and after each set of attempted parameters
  on a Theory, and each set of parameters is run on a new instance of the test class.
  
- Exposed API's `ParameterSignature.getType()` and `ParameterSignature.getAnnotations()`

- An array of data points can be introduced by a field or method marked with the new annotation `@DataPoints`

- The Theories custom runner has been refactored to make it easier to extend

### JUnit 4 Runner API ###

- There has been a drastic rewrite of the API for custom Runners in 4.5.  This
  needs to be written up separately before release.
  
- Tests with failed assumptions are now marked as Ignored, rather than silently passing.
  This may change behavior in some client tests, and also will require some new support 
  on the part of IDE's.
  
## Summary of Changes in version 4.4 ##

JUnit is designed to efficiently capture developers' intentions about
their code, and quickly check their code matches those intentions.
Over the last year, we've been talking about what things developers
would like to say about their code that have been difficult in the
past, and how we can make them easier.

[Download][]

[Download]: http://sourceforge.net/project/showfiles.php?group_id=15278

### assertThat ###

Two years ago, Joe Walnes built a [new assertion mechanism][walnes] on top of what was 
then [JMock 1][].  The method name was `assertThat`, and the syntax looked like this:

[walnes]: http://joewalnes.com/2005/05/13/flexible-junit-assertions-with-assertthat/
[JMock 1]: http://www.jmock.org/download.html

```java
assertThat(x, is(3));
assertThat(x, is(not(4)));
assertThat(responseString, either(containsString("color")).or(containsString("colour")));
assertThat(myList, hasItem("3"));
```

More generally:

```java
assertThat([value], [matcher statement]);
```

Advantages of this assertion syntax include:

- More readable and typeable: this syntax allows you to think in terms of subject, verb, object
  (assert "x is 3") rather than `assertEquals`, which uses verb, object, subject (assert "equals 3 x")

- Combinations: any matcher statement `s` can be negated (`not(s)`), combined (`either(s).or(t)`),
  mapped to a collection (`each(s)`), or used in custom combinations (`afterFiveSeconds(s)`)

- Readable failure messages.  Compare

```java
assertTrue(responseString.contains("color") || responseString.contains("colour"));
// ==> failure message: 
// java.lang.AssertionError:

assertThat(responseString, anyOf(containsString("color"), containsString("colour")));
// ==> failure message:
// java.lang.AssertionError: 
// Expected: (a string containing "color" or a string containing "colour")
//      got: "Please choose a font"
```

- Custom Matchers.  By implementing the `Matcher` interface yourself, you can get all of the
  above benefits for your own custom assertions.

- For a more thorough description of these points, see [Joe Walnes's
  original post][walnes].

We have decided to include this API directly in JUnit.
It's an extensible and readable syntax, and it enables
new features, like [assumptions][] and [theories][].

[assumptions]: #assumptions
[theories]: #theories

Some notes:

- The old assert methods are never, ever, going away.  Developers may 
  continue using the old `assertEquals`, `assertTrue`, and so on.
- The second parameter of an `assertThat` statement is a `Matcher`.
  We include the Matchers we want as static imports, like this:

```java
import static org.hamcrest.CoreMatchers.is;
```

  or:

```java
import static org.hamcrest.CoreMatchers.*;
```

- Manually importing `Matcher` methods can be frustrating.  [Eclipse 3.3][] includes the ability to 
  define
  "Favorite" classes to import static methods from, which makes it easier 
  (Search for "Favorites" in the Preferences dialog).
  We expect that support for static imports will improve in all Java IDEs in the future.

[Eclipse 3.3]: http://www.eclipse.org/downloads/

- To allow compatibility with a wide variety of possible matchers, 
  we have decided to include the classes from hamcrest-core,
  from the [Hamcrest][] project.  This is the first time that
  third-party classes have been included in JUnit.  

[Hamcrest]: http://code.google.com/p/hamcrest/

- JUnit currently ships with a few matchers, defined in 
  `org.hamcrest.CoreMatchers` and `org.junit.matchers.JUnitMatchers`.  
  To use many, many more, consider downloading the [full hamcrest package][].

[full hamcrest package]: http://hamcrest.googlecode.com/files/hamcrest-all-1.1.jar

- JUnit contains special support for comparing string and array
  values, giving specific information on how they differ.  This is not
  yet available using the `assertThat` syntax, but we hope to bring
  the two assert methods into closer alignment in future releases.

<a name="assumptions" />
### Assumptions ###

Ideally, the developer writing a test has control of all of the forces that might cause a test to fail.
If this isn't immediately possible, making dependencies explicit can often improve a design.  
For example, if a test fails when run in a different locale than the developer intended,
it can be fixed by explicitly passing a locale to the domain code.

However, sometimes this is not desirable or possible.  
It's good to be able to run a test against the code as it is currently written, 
implicit assumptions and all, or to write a test that exposes a known bug.
For these situations, JUnit now includes the ability to express "assumptions":

```java
import static org.junit.Assume.*

@Test public void filenameIncludesUsername() {
    assumeThat(File.separatorChar, is('/'));
    assertThat(new User("optimus").configFileName(), is("configfiles/optimus.cfg"));
}

@Test public void correctBehaviorWhenFilenameIsNull() {
    assumeTrue(bugFixed("13356"));  // bugFixed is not included in JUnit
    assertThat(parse(null), is(new NullDocument()));
}
```

With this release, a failed assumption will lead to the test being marked as passing,
regardless of what the code below the assumption may assert.
In the future, this may change, and a failed assumption may lead to the test being ignored:
however, third-party runners do not currently allow this option.

We have included `assumeTrue` for convenience, but thanks to the
inclusion of Hamcrest, we do not need to create `assumeEquals`,
`assumeSame`, and other analogues to the `assert*` methods.  All of
those functionalities are subsumed in `assumeThat`, with the appropriate
matcher.

A failing assumption in a `@Before` or `@BeforeClass` method will have the same effect
as a failing assumption in each `@Test` method of the class.

<a name="theories" />
### Theories ###

More flexible and expressive assertions, combined with the ability to
state assumptions clearly, lead to a new kind of statement of intent, 
which we call a "Theory".  A test captures the intended behavior in
one particular scenario.  A theory captures some aspect of the
intended behavior in possibly
infinite numbers of potential scenarios.  For example:

```java
@RunWith(Theories.class)
public class UserTest {
    @DataPoint public static String GOOD_USERNAME = "optimus";
    @DataPoint public static String USERNAME_WITH_SLASH = "optimus/prime";

    @Theory public void filenameIncludesUsername(String username) {
        assumeThat(username, not(containsString("/")));
        assertThat(new User(username).configFileName(), containsString(username));
    }
}
```

This makes it clear that the user's filename should be included in the
config file name, only if it doesn't contain a slash.  Another test
or theory might define what happens when a username does contain a slash.

`UserTest` will attempt to run `filenameIncludesUsername` on 
every compatible `DataPoint` defined in the class.  If any of the
assumptions fail, the data point is silently ignored.  If all of the
assumptions pass, but an assertion fails, the test fails.

The support for Theories has been absorbed from the [Popper][]
project, and [more complete documentation][popper-docs] can be found
there.

[Popper]: http://popper.tigris.org
[popper-docs]: http://popper.tigris.org/tutorial.html

Defining general statements in this way can jog the developer's memory
about other potential data points and tests, also allows [automated
tools][junit-factory] to [search][my-blog] for new, unexpected data
points that expose bugs.

[junit-factory]: http://www.junitfactory.org
[my-blog]: http://shareandenjoy.saff.net/2007/04/popper-and-junitfactory.html

### Other changes ###

This release contains other bug fixes and new features.  Among them:

- Annotated descriptions

  Runner UIs, Filters, and Sorters operate on Descriptions of test
  methods and test classes.  These Descriptions now include the
  annotations on the original Java source element, allowing for richer
  display of test results, and easier development of annotation-based
  filters.

- Bug fix (1715326): assertEquals now compares all Numbers using their
  native implementation of `equals`.  This assertion, which passed in
  4.3, will now fail:

```java
assertEquals(new Integer(1), new Long(1));
```

  Non-integer Numbers (Floats, Doubles, BigDecimals, etc),
  which were compared incorrectly in 4.3, are now fixed.

- `assertEquals(long, long)` and `assertEquals(double, double)` have
  been re-introduced to the `Assert` class, to take advantage of
  Java's native widening conversions.  Therefore, this still passes:

```java
assertEquals(1, 1L);
```

- The default runner for JUnit 4 test classes has been refactored.
  The old version was named `TestClassRunner`, and the new is named
  `JUnit4ClassRunner`.  Likewise, `OldTestClassRunner` is now
  `JUnit3ClassRunner`.  The new design allows variations in running
  individual test classes to be expressed with fewer custom classes.
  For a good example, see the source to
  `org.junit.experimental.theories.Theories`.

- The rules for determining which runner is applied by default to a
  test class have been simplified:

  1. If the class has a `@RunWith` annotation, the annotated runner
     class is used.

  2. If the class can be run with the JUnit 3 test runner (it
     subclasses `TestCase`, or contains a `public static Test suite()`
     method), JUnit38ClassRunner is used.

  3. Otherwise, JUnit4ClassRunner is used.

  This default guess can always be overridden by an explicit
  `@RunWith(JUnit4ClassRunner.class)` or
  `@RunWith(JUnit38ClassRunner.class)` annotation.

  The old class names `TestClassRunner` and `OldTestClassRunner`
  remain as deprecated.

- Bug fix (1739095): Filters and Sorters work correctly on test
  classes that contain a `suite` method like:

```java
public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(MyTest.class);
}
```

- Bug fix (1745048): @After methods are now correctly called 
  after a test method times out.

