## Summary of Changes in version 4.5 ##

### Installation ###

- We are releasing `junit-4.5.jar`, which contains all the classes
  necessary to run JUnit, and `junit-dep-4.5.jar`, which leaves out
  hamcrest classes, for developers who already use hamcrest outside of
  JUnit.

### Basic JUnit operation ###

- JUnitCore now more often exits with the correct exit code (0 for
  success, 1 for failure)

- Badly formed test classes (exceptions in constructors, classes
  without tests, multiple constructors, Suite without @SuiteClasses)
  produce more helpful error messages

- Test classes whose only test methods are inherited from superclasses
  now run.

- Optimization to annotation processing can cut JUnit overhead by more than half
  on large test classes, especially when using Theories.  [Bug 1796847]

- A failing assumption in a constructor ignores the class

- Correct results when comparing the string "null" with potentially
  null values.  [Bug 1857283]

- Annotating a class with `@RunWith(JUnit4.class)` will always invoke the
  default JUnit 4 runner in the current version of JUnit.  This default changed
  from `JUnit4ClassRunner` in 4.4 to `BlockJUnit4ClassRunner` in 4.5 (see below),
  and may change again.

### Extension ###

- `BlockJUnit4Runner` is a new implementation of the standard JUnit 4
  test class functionality.  In contrast to `JUnit4ClassRunner` (the old
  implementation):

  - `BlockJUnit4Runner` has a much simpler implementation based on
    Statements, allowing new operations to be inserted into the
    appropriate point in the execution flow.

  - `BlockJUnit4Runner` is published, and extension and reuse are
    encouraged, whereas `JUnit4ClassRunner` was in an internal package,
    and is now deprecated.

- `ParentRunner` is a base class for runners that iterate over
  a list of "children", each an object representing a test or suite to run.
  `ParentRunner` provides filtering, sorting, `@BeforeClass`, `@AfterClass`,
  and method validation to subclasses.

- `TestClass` wraps a class to be run, providing efficient, repeated access
  to all methods with a given annotation.

- The new `RunnerBuilder` API allows extending the behavior of
  Suite-like custom runners.

- `AssumptionViolatedException.toString()` is more informative

### Extra Runners ###

- `Parameterized.eachOne()` has been removed

- New runner `Enclosed` runs all static inner classes of an outer class.

### Theories ###

- `@Before` and `@After` methods are run before and after each set of attempted parameters
  on a Theory, and each set of parameters is run on a new instance of the test class.
  
- Exposed API's `ParameterSignature.getType()` and `ParameterSignature.getAnnotations()`

- An array of data points can be introduced by a field or method
  marked with the new annotation `@DataPoints`

- The Theories custom runner has been refactored to make it faster and
  easier to extend

### Development ###

- Source has been split into directories `src/main/java` and
  `src/test/java`, making it easier to exclude tests from builds, and
  making JUnit more maven-friendly

- Test classes in `org.junit.tests` have been organized into
  subpackages, hopefully making finding tests easier.

- `ResultMatchers` has more informative descriptions.

- `TestSystem` allows testing return codes and other system-level interactions.

### Incompatible changes ###

- Removed Request.classes(String, Class<?>...) factory method
