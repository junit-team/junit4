## Summary of changes in version 4.12

# Assertions

### [Pull request #611:](https://github.com/junit-team/junit4/pull/611) Assert.assertNotEquals() for `float` parameters

Version 4.11 added `Assert.assertEquals()` for `float` parameters with a delta, and `Assert.assertNotEquals()`. This is the combination of those two features.


### [Pull request #632:](https://github.com/junit-team/junit4/pull/632) Assert.assertArrayEquals() for `boolean[]` parameters.

`Assert.assertArrayEquals()` previously existed for all primitive array types, except `boolean[]`. This has now been added for `boolean[]`.


### [Pull request #918:](https://github.com/junit-team/junit4/pull/918) Avoid potentially expensive reflection-based loop in Assert.assertArrayEquals()

In the usual case, where the array elements are in fact exactly equal, the potentially expensive reflection-based loop to compare them is avoided by using `Arrays.deepEquals()` first. The exact comparison is only executed when `deepEquals()` returns `false`.


# Command-line options
### [Pull request #647:](https://github.com/junit-team/junit4/pull/647) Support command-line `--filter` param.

When running JUnit from the command line, a command-line parameter can be supplied using `--filter`, which supplies a filter that will restrict which tests and subtests from the rest of the command will be run.  For example, this will run only the tests in ExampleTestSuite that are in categories Cat1 or Cat2:

```
java org.junit.runner.JUnitCore \
  --filter=org.junit.experimental.categories.IncludeCategories=pkg.of.Cat1,pkg.of.Cat2 \
  com.example.ExampleTestSuite
```

In general, the argument to `--filter` should be `ClassName=param`, where `ClassName` names an implementation of `FilterFactory`, whose `createFilter` method will be called with an instance of `FilterFactoryParams` that contains `"param"`, in order to return the filter to be applied.

# Test Runners


### [Pull request #763:](https://github.com/junit-team/junit4/pull/763) Allow custom test runners to create their own TestClasses and customize the scanning of annotations.

This introduces some extension points to `ParentRunner` to allow subclasses to control creation
of the `TestClass` instance and to scan for annotations.

### [Pull request #817:](https://github.com/junit-team/junit4/pull/817) Support for context hierarchies

The `AnnotatedBuilder` is a strategy for constructing runners for test classes that have been annotated with the `@RunWith` annotation. All tests within such a class will be executed using the runner that was specified within the annotation.

Prior to JUnit 4.12, this covered only the tests within the annotated test class. With 4.12, the `AnnotationBuilder` will also support inner member classes. If a custom test runner supports inner member classes (which JUnit does not support out-of-the-box), the member classes will inherit the runner from the enclosing class, e.g.:

```java
@RunWith(MyRunner.class)
public class MyTest {
    // some tests might go here

    public class MyMemberClass {
        @Test
        public void thisTestRunsWith_MyRunner() {
            // some test logic
        }

        // some more tests might go here
    }

    @RunWith(AnotherRunner.class)
    public class AnotherMemberClass {
        // some tests might go here

        public class DeepInnerClass {
            @Test
            public void thisTestRunsWith_AnotherRunner() {
                // some test logic
            }
        }

        public class DeepInheritedClass extends SuperTest {
            @Test
            public void thisTestRunsWith_SuperRunner() {
                // some test logic
            }
        }
    }
}

@RunWith(SuperRunner.class)
public class SuperTest {
    // some tests might go here
}
```

The key points to note here are:

* If there is no `@RunWith` annotation, no runner will be created.
* The resolve step is inside-out, e.g. the closest `@RunWith` annotation wins.
* `@RunWith` annotations are inherited and work as if the class was annotated itself.
* The default JUnit runner does not support inner member classes, so this is only valid for custom runners that support inner member classes.
* Custom runners with support for inner classes may or may not support `@RunWith` annotations for member classes. Please refer to the custom runner documentation.

One example of a runner that makes use of this extension is the Hierarchical Context Runner (see https://github.com/bechte/junit-hierarchicalcontextrunner/wiki).


### [Pull request #716:](https://github.com/junit-team/junit4/pull/716) Fix annotation collection from superclasses of JUnit3 tests.

Previously `Description.getAnnotations()` would always return an empty list for _test*_ methods derived from superclasses. 


### [Pull request #625 (commit 72af03c49f):](https://github.com/junit-team/junit4/commit/72af03c49fdad5f10e36c7eb4e7045feb971d253) Make `RunNotifier` code concurrent.

When running tests from multiple threads, JUnit will now call `RunListener` methods from multiple threads if the listener class is annotated with `@RunListener.ThreadSafe`. In addition, the code in `RunNotifier` has been modified to not use locks.


### [Pull request #684:](https://github.com/junit-team/junit4/pull/684) Adding `AnnotationValidator` framework and validation checks for `@Category`.

This allows for validation to be added to annotations. Validators should extend `AnnotationValidator` and be attached to annotations with the `@ValidateWith` annotation. `CategoryValidator` extends `AnnotationValidator` and ensures that incompatible annotations (`@BeforeClass`, `@AfterClass`, `@Before`, `@After`) are not used in conjunction with `@Category`.


# Exception Testing


### [Pull request #583:](https://github.com/junit-team/junit4/pull/583) [Pull request #720:](https://github.com/junit-team/junit4/pull/720) Fix handling of `AssertionError` and `AssumptionViolatedException` in `ExpectedException` rule.

`ExpectedException` didn't handle `AssertionError`s and `AssumptionViolatedException` well. This has been fixed. The new documentation explains the usage of `ExpectedException` for testing these exceptions. The two methods `handleAssertionErrors()` and `handleAssumptionViolatedExceptions()` are not needed anymore. If you have used them, just remove it and read `ExpectedException`'s documentation.


### [Pull request #818:](https://github.com/junit-team/junit4/pull/818) [Pull request #993:](https://github.com/junit-team/junit4/pull/993) External version of AssumptionViolatedException

In JUnit 4.11 and earlier, if you wanted to write a custom runner that handled
`AssumptionViolatedException` or you needed to create an instance of `AssumptionViolatedException`
directly, you needed to import an internal class (`org.junit.internal.AssumptionViolatedException`).
Now you can import `org.junit.AssumptionViolatedException` (which extends
`org.junit.internal.AssumptionViolatedException`).

The classes in `Assume` have been modified to throw `org.junit.AssumptionViolatedException`.

The constructors in the external `AssumptionViolatedException` are also
simpler than the ones in the internal version. That being said,
it's recommended that you create `AssumptionViolatedException` via the methods in `Assume`.


### [Pull request #985:](https://github.com/junit-team/junit4/pull/985) Change AssumptionViolatedException to not set the cause to null; fixes issue #494

Previously, the `AssumptionViolatedException` constructors would explicitly set the cause to `null`
(unless you use a constructor where you provide a `Throwable`, in which case it would set that as
the cause). This prevented code directly creating the exception from setting a cause.

With this change, the cause is only set if you pass in a `Throwable`.

It's recommended that you create `AssumptionViolatedException` via the methods in `Assume`.


### [Pull request #542:](https://github.com/junit-team/junit4/pull/542) Customized failure message for `ExpectedException`

`ExpectedException` now allows customization of the failure message when the test does not throw the expected exception. For example:

```java
thrown.reportMissingExceptionWithMessage("FAIL: Expected exception to be thrown");
```

If a custom failure message is not provided, a default message is used.


### [Pull request #1013:](https://github.com/junit-team/junit4/pull/1013) Make ErrorCollector#checkSucceeds generic

The method `ErrorCollector.checkSucceeds()` is now generic. Previously, you could only pass
in a `Callable<Object>` and it returned `Object`. You can now pass any `Callable` and the
return type will match the type of the callable.


# Timeout for Tests
*See also [Timeout for tests](https://github.com/junit-team/junit4/wiki/Timeout-for-tests)*

### [Pull request #823:](https://github.com/junit-team/junit4/pull/823) Throw `TestFailedOnTimeoutException` instead of plain `Exception` on timeout

When a test times out, a `org.junit.runners.model.TestTimedOutException` is now thrown instead of a plain `java.lang.Exception`.


### [Pull request #742:](https://github.com/junit-team/junit4/pull/742) [Pull request #986:](https://github.com/junit-team/junit4/pull/986) `Timeout` exceptions now include stack trace from stuck thread (experimental)

`Timeout` exceptions try to determine if there is a child thread causing the problem, and if so its stack trace is included in the exception in addition to the one of the main thread. This feature must be enabled with the timeout rule by creating it through the new `Timeout.builder()` method:

```java
public class HasGlobalTimeout {
    @Rule public final TestRule timeout = Timeout.builder()
            .withTimeout(10, TimeUnit.SECONDS)
            .withLookingForStuckThread(true)
            .build();

    @Test
    public void testInfiniteLoop() {
        for (;;) {
        }
    }
}
```


### [Pull request #544:](https://github.com/junit-team/junit4/pull/544) New constructor and factories in `Timeout`

`Timeout` deprecated the old constructor `Timeout(int millis)`.
A new constructor is available: `Timeout(long timeout, TimeUnit unit)`. It enables you to use different granularities of time units like `NANOSECONDS`, `MICROSECONDS`, `MILLISECONDS`, and `SECONDS`. Examples:

```java
@Rule public final TestRule globalTimeout = new Timeout(50, TimeUnit.MILLISECONDS);
```

```java
@Rule public final TestRule globalTimeout = new Timeout(10, TimeUnit.SECONDS);
```

and factory methods in `Timeout`:

```java
@Rule public final TestRule globalTimeout = Timeout.millis(50);
```

```java
@Rule public final TestRule globalTimeout = Timeout.seconds(10);
```

This usage avoids the truncation, which was the problem in the deprecated constructor `Timeout(int millis)` when casting `long` to `int`.


### [Pull request #549:](https://github.com/junit-team/junit4/pull/549) fixes for #544 and #545

The `Timeout` rule applies the same timeout to all test methods in a class:

```java
public class HasGlobalTimeout {
    @Rule
    public Timeout globalTimeout = new Timeout(10, TimeUnit.SECONDS);

    @Test
    public void testInfiniteLoop() {
        for (;;) {
        }
    }

    @Test
    public synchronized void testInterruptableLock() throws InterruptedException {
        wait();
    }

    @Test
    public void testInterruptableIO() throws IOException {
        for (;;) {
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();

            // Interrupted thread closes channel and throws ClosedByInterruptException.
            channel.write(buffer);
            channel.close();
        }
    }
}
```
Each test is run in a new _daemon_ thread. If the specified timeout elapses before the test completes, its execution is interrupted via `Thread#interrupt()`. This happens in interruptable I/O (operations throwing `java.io.InterruptedIOException` and `java.nio.channels.ClosedByInterruptException`), locks (package `java.util.concurrent`) and methods in `java.lang.Object` and `java.lang.Thread` throwing `java.lang.InterruptedException`.

### [Pull request #876:](https://github.com/junit-team/junit4/pull/876) The timeout rule never times out if you pass in a timeout of zero.

A specified timeout of 0 will be interpreted as not set, however tests still launch from separate threads. This can be useful for disabling timeouts in environments where they are dynamically set based on some property.


# Parameterized Tests


### [Pull request #702:](https://github.com/junit-team/junit4/pull/702) Support more return types for the `@Parameters` method of the `Parameterized` runner

The return types `Iterator<? extends Object>`, `Object[]` and `Object[][]` are now supported on methods annotated with `@Parameters`. You don't have to wrap arrays with `Iterable`s and single parameters with `Object` arrays.


### [Pull request #773:](https://github.com/junit-team/junit4/pull/773) Allow configurable creation of child runners of parameterized suites

The factory for creating the `Runner` instance of a single set of parameters is now configurable. It can be specified by the `@UseParametersRunnerFactory` annotation.


# Rules


### [Pull request #552:](https://github.com/junit-team/junit4/pull/552) [Pull request #937:](https://github.com/junit-team/junit4/pull/937) `Stopwatch` rule

The `Stopwatch` Rule notifies one of its own protected methods of the time spent by a test. Override them to get the time in nanoseconds. For example, this class will keep logging the time spent by each passed, failed, skipped, and finished test:

```java
public static class StopwatchTest {
    private static final Logger logger = Logger.getLogger("");

    private static void logInfo(String testName, String status, long nanos) {
        logger.info(String.format("Test %s %s, spent %d microseconds",
            testName, status, Stopwatch.toMicros(nanos)));
    }

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void succeeded(long nanos, Description description) {
            logInfo(description.getMethodName(), "succeeded", nanos);
        }

        @Override
        protected void failed(long nanos, Throwable e, Description description) {
            logInfo(description.getMethodName(), "failed", nanos);
        }

        @Override
        protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
            logInfo(description.getMethodName(), "skipped", nanos);
        }

        @Override
        protected void finished(long nanos, Description description) {
            logInfo(description.getMethodName(), "finished", nanos);
        }
    };

    @Test
    public void succeeds() {
    }

    @Test
    public void fails() {
        fail();
    }

    @Test
    public void skips() {
        assumeTrue(false);
    }
}
```

An example to assert running time:

```java
@Test
public void performanceTest() throws InterruptedException {
    long delta = 30;
    Thread.sleep(300L);
    assertEquals(300D, stopwatch.runtime(MILLISECONDS), delta);
    Thread.sleep(500L);
    assertEquals(800D, stopwatch.runtime(MILLISECONDS), delta);
}
```

### [Pull request #932:](https://github.com/junit-team/junit4/pull/932) Allow static `@Rule`s also annotated with `@ClassRule`

JUnit 4.11 introduced restrictions requiring `@Rule` members to be non-static and `@ClassRule` members to be static. These restrictions have been relaxed slightly, in that a static member annotated with both `@Rule` and `@ClassRule` is now considered valid. This means a single rule may be used to perform actions both before/after a class (e.g. setup/tear down an external resource) and between tests (e.g. reset the external resource), without the need for any workarounds mentioned in issue [#793](https://github.com/junit-team/junit4/issues/793).

Note that a non-static `@ClassRule` annotated member is still considered invalid, even if annotated with `@Rule`.

```java
public class CommonRuleTest {
    @Rule
    @ClassRule
    public static MySetupResetAndTearDownRule rule = new MySetupResetAndTearDownRule();
}
```

Be warned that if you have static methods or fields annotated with `@Rule` you will not be able to run your test methods in parallel.

### [Pull request #956:](https://github.com/junit-team/junit4/pull/956) `DisableOnDebug` rule

The `DisableOnDebug` rule allows users to disable other rules when the JVM is launched in debug mode. Prior to this feature the common approach to disable rules that make debugging difficult was to comment them out and remember to revert the change. When using this feature users no longer have to modify their test code nor do they need to remember to revert changes.

This rule is particularly useful in combination with the `Timeout` rule. 

```
@Rule
public DisableOnDebug timeout = new DisableOnDebug(Timeout.seconds(1));
```

See the Javadoc for more detail and limitations. Related to https://github.com/junit-team/junit4/issues/738

### [Pull request #974:](https://github.com/junit-team/junit4/pull/974) Updated `TemporaryFolder.newFolder()` to give an error message if a path contains a slash.

If you call `TemporaryFolder.newFolder("foo/bar")` in JUnit 4.10 the method returns a `File` object for the new folder but actually fails to create it. That is contrary to the expected behaviour of the method which is to actually create the folder. In JUnit 4.11 the same call throws an exception. Nowhere in the documentation does it explain that the String(s) passed to that method can only be single path components.

With this fix, folder names are validated to contain single path name. If the folder name consists of multiple path names, an exception is thrown stating that usage of multiple path components in a string containing folder name is disallowed.

### [Pull request #1015:](https://github.com/junit-team/junit4/pull/1015) Methods annotated with `Rule` can return a `MethodRule`.

Methods annotated with `@Rule` can now return either a `TestRule` (or subclass) or a
`MethodRule` (or subclass).

Prior to this change, all public methods annotated with `@Rule` were called, but the
return value was ignored if it could not be assigned to a `TestRule`. After this change,
the method is only called if the return type could be assigned to `TestRule` or
`MethodRule`. For methods annotated with `@Rule` that return other values, see the notes
for pull request #1020.

### [Pull request #1020:](https://github.com/junit-team/junit4/pull/1020) Added validation that @ClassRule should only be implementation of TestRule.

Prior to this change, fields annotated with `@ClassRule` that did not have a type of `TestRule`
(or a class that implements `TestRule`) were ignored. With this change, the test will fail
with a validation error.

Prior to this change, methods annotated with `@ClassRule` that did specify a return type
of `TestRule`(or a class that implements `TestRule`) were ignored. With this change, the test
will fail with a validation error.

### [Pull request #1021:](https://github.com/junit-team/junit4/pull/1021) JavaDoc of TemporaryFolder: folder not guaranteed to be deleted.

Adjusted JavaDoc of TemporaryFolder to reflect that temporary folders are not guaranteed to be
deleted.

# Theories


### [Pull request #529:](https://github.com/junit-team/junit4/pull/529) `@DataPoints`-annotated methods can now yield `null` values

Up until JUnit 4.11 a `@DataPoints`-annotated array field could contain `null` values, but the array returned by a `@DataPoints`-annotated method could not. This asymmetry has been resolved: _both_ can now provide a `null` data point. 


### [Pull request #572:](https://github.com/junit-team/junit4/pull/572) Ensuring no-generic-type-parms validator called/tested for theories

The `Theories` runner now disallows `Theory` methods with parameters that have "unresolved" generic type parameters (e.g. `List<T>` where `T` is a type variable). It is exceedingly difficult for the `DataPoint(s)` scraper or other `ParameterSupplier`s to correctly decide values that can legitimately be assigned to such parameters in a type-safe way, so JUnit now disallows them altogether. Theory parameters such as `List<String>` and `Iterable<? extends Number>` are still allowed.

The machinery to perform this validation was in the code base for some time, but not used. It now is used.

[junit.contrib](https://github.com/junit-team/junit.contrib)'s rendition of theories performs the same validation.


### [Pull request #607:](https://github.com/junit-team/junit4/pull/607) Improving theory failure messages

Theory failure messages previously were of the form: `ParameterizedAssertionError: theoryTest(badDatapoint, allValues[1], otherVar)`, where allValues, badDatapoint and otherVar were the variables the datapoints was sourced from. These messages are now of the form: 

```java
ParameterizedAssertionError: theoryTest(null <from badDatapoint>, "good value" <from allValues[1]>, 
                                  [toString() threw RuntimeException: Error message] <from otherVar>)
```


### [Pull request #601:](https://github.com/junit-team/junit4/pull/601) Allow use of `Assume` in tests run by `Theories` runner

If, in a theory, all parameters were "assumed" away, the `Theories` runner would properly fail, informing you that no parameters were found to actually test something. However, if you had another method in that same class, that was not a theory (annotated with `@Test` only,) you could not use Assume in that test. Now, the `Theories` runner will verify the method is annotated with `@Theory` before failing due to no parameters being found.

```java
@RunWith(Theories.class)
public class TheoriesAndTestsTogether {
    @DataPoint
    public static Object o;

    @Theory
    public void theory(Object o) {
        // this will still fail: java.lang.AssertionError: Never found parameters that satisfied method assumptions.
        Assume.assumeTrue(false);
    }

    @Test
    public void test() {
        // this will no longer fail
        Assume.assumeTrue(false);
    }
}
```


### [Pull request #623:](https://github.com/junit-team/junit4/pull/623) Ensure data points array fields and methods are `public` and `static` in Theory classes.

Previously if a data points array field or method was non-`static` or non-`public` it would be silently ignored and the data points not used. Now the `Theories` runner verifies that all `@DataPoint` or `@DataPoints` annotated fields or methods in classes are both `public` and `static`, and such classes will fail to run with `InitializationError`s if they are not.


### [Pull request #621:](https://github.com/junit-team/junit4/pull/621) Added mechanism for matching specific data points in theories to specific parameters, by naming data points.

`@DataPoints` fields or methods can now be given (one or more) names in the annotation, and `@Theory` method parameters can be annotated with `@FromDataPoints(name)`, to limit the data points considered for that parameter to only the data points with that name:

```java
@DataPoints
public static String[] unnamed = new String[] { ... };

@DataPoints("regexes")
public static String[] regexStrings = new String[] { ... };
  
@DataPoints({"forMatching", "alphanumeric"})
public static String[] testStrings = new String[] { ... }; 
  
@Theory
public void stringTheory(String param) {
    // This will be called with every value in 'regexStrings',
    // 'testStrings' and 'unnamed'.
}
  
@Theory
public void regexTheory(@FromDataPoints("regexes") String regex,
                        @FromDataPoints("forMatching") String value) {
    // This will be called with only the values in 'regexStrings' as 
    // regex, only the values in 'testStrings' as value, and none 
    // of the values in 'unnamed'.
}
```


### [Pull request #654:](https://github.com/junit-team/junit4/pull/654) Auto-generation of `enum` and `boolean` data points

Any theory method parameters with `boolean` or `enum` types that cannot be supplied with values by any other sources will be automatically supplied with default values: `true` and `false`, or every value of the given `enum`. If other explicitly defined values are available (e.g. from a specified `ParameterSupplier` or some `DataPoints` method in the theory class), only those explicitly defined values will be used.


### [Pull request #651:](https://github.com/junit-team/junit4/pull/651) Improvements to Theory parameter and DataPoint type matching

 * Validity of `DataPoints` for theory parameters for all field data points and multi-valued method data points (i.e. not single-valued method data points) is now done on runtime type, not field/method return type (previously this was the case for multi-valued array methods only).

 * Validity of `DataPoints` for theory parameters for all data points now correctly handles boxing and unboxing for primitive and wrapper types; e.g. `int` values will be considered for theory parameters that are `Integer` assignable, and vice versa.


### [Pull request #639:](https://github.com/junit-team/junit4/pull/639) Failing theory datapoint methods now cause theory test failures

Previously `@DataPoint(s)` methods that threw exceptions were quietly ignored and if another `DataPoint` source was available then those values alone were used, leaving the theory passing using only a subset of the (presumably) intended input values. Now, any data point method failures during invocation of a theory will cause the theory being tested to fail immediately.

*This is a non-backward-compatible change*, and could potentially break theory tests that depended on failing methods. If that was desired behavior, then the expected exceptions can instead be specifically ignored using the new `ignoredExceptions` array attribute on `@DataPoint` and `@DataPoints` methods. Adding an exception to this `ignoredExceptions` array will stop theory methods from failing if the given exception, or subclasses of it, are thrown in the annotated method. This attribute has no effect on data point fields.


### [Pull request #658:](https://github.com/junit-team/junit4/pull/658) `Iterable`s can now be used as data points

Previously, when building sets of data points for theory parameters, the only valid multi-valued `@DataPoints` types were arrays. This has now been extended to also take parameters from `Iterable` `@DataPoints` methods and fields.


# Categories


### [Pull request #566:](https://github.com/junit-team/junit4/pull/566) Enables inheritance on `Category` by adding `@Inherited`

`@interface Category` now is annotated with `@Inherited` itself. This enables inheritance of categories from ancestors (e.g. abstract test-classes). Note that you are able to "overwrite" `@Category` on inheritors and that this has no effect on method-level categories (see [@Inherited](http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/annotation/Inherited.html)).


### [Pull request #503:](https://github.com/junit-team/junit4/pull/503) Configurable Categories

From a given set of test classes, the `Categories` runner runs only the classes and methods
that are annotated with either the category given with the `@IncludeCategory` annotation, or a subtype of that category. Either classes or interfaces can be used as categories. Subtyping works, so if you say `@IncludeCategory(SuperClass.class)`, a test marked `@Category({SubClass.class})` will be run.

You can also exclude categories by using the `@ExcludeCategory` annotation; see `SlowTestSuiteWithoutFast`.

The suite `FastOrSmokeTestSuite` is an example to run multiple categories.

To execute tests which match all categories, use `matchAny = false` in annotations. See `FastAndSmokeTestSuite`.
 
Example:

```java
public static interface FastTests { /* category marker */ }
public static interface SlowTests { /* category marker */ }
public static interface SmokeTests { /* category marker */ }

public static class A {
    public void a() {
        fail();
    }

    @Category(SlowTests.class)
    @Test
    public void b() {
    }

    @Category({FastTests.class, SmokeTests.class})
    @Test
    public void c() {
    }
}

@Category({SlowTests.class, FastTests.class})
public static class B {
    @Test
    public void d() {
    }
}

@RunWith(Categories.class)
@Categories.IncludeCategory(SlowTests.class)
@Suite.SuiteClasses({A.class, B.class})
public static class SlowTestSuite {
    // Will run A.b and B.d, but not A.a and A.c
}

@RunWith(Categories.class)
@Categories.IncludeCategory({FastTests.class, SmokeTests.class})
@Suite.SuiteClasses({A.class, B.class})
public static class FastOrSmokeTestSuite {
    // Will run A.c and B.d, but not A.b because it is not any of FastTests or SmokeTests
}

@RunWith(Categories.class)
@Categories.IncludeCategory(value = {FastTests.class, SmokeTests.class}, matchAny = false)
@Suite.SuiteClasses({A.class, B.class})
public static class FastAndSmokeTestSuite {
    // Will run only A.c => match both FastTests AND SmokeTests
}

@RunWith(Categories.class)
@Categories.IncludeCategory(SlowTests.class)
@Categories.ExcludeCategory(FastTests.class)
@Suite.SuiteClasses({A.class, B.class}) // Note that Categories is a kind of Suite
public class SlowTestSuiteWithoutFast {
	// Will run A.b, but not A.a, A.c or B.d
}
```


# Use with Maven


### [Pull request #879:] (https://github.com/junit-team/junit4/pull/879) Add the default 'Implementation-*' headers to the manifest

The default Maven-style 'Implementation-*' headers are now present in the manifest of `junit.jar`. Example:
```
Implementation-Vendor: JUnit
Implementation-Title: JUnit
Implementation-Version: 4.12
Implementation-Vendor-Id: junit
```


### [Pull request #511:](https://github.com/junit-team/junit4/pull/511) Maven project junit:junit:jar


#### How to install Maven

Download the Maven binary [http://www.us.apache.org/dist/maven/maven-3/3.0.4/binaries](http://www.us.apache.org/dist/maven/maven-3/3.0.4/binaries).

(wget http://www.us.apache.org/dist/maven/maven-3/3.0.4/binaries/apache-maven-3.0.4-bin.tar.gz)

If you are in the project root, extract the archive (tar xvzf apache-maven-3.0.4-bin.tar.gz).
Create directory _.m2_ in your _user home_. Then the artifacts and plugins are stored in `~/.m2/repository`.
( _~_ stands for user home)


#### How to launch the build from the command line

Clone the project (git clone https://github.com/junit-team/junit4.git) and navigate to the project root on your local system (cd junit).
Clean the previous build in _target_ directory, build the project, and install new artifacts in your local repository:

`apache-maven-3.0.4/bin/mvn clean install`

On Windows type the command `apache-maven-3.0.4\bin\mvn clean install`.

Set the environment variables `M2_HOME` and `PATH` when frequently building via command line `mvn clean install`.

[http://maven.apache.org/guides/development/guide-building-m2.html#Building_Maven_Without_Maven_Installed](http://maven.apache.org/guides/development/guide-building-m2.html#Building_Maven_Without_Maven_Installed)


#### How to install and build the Maven project in Eclipse

I made a clone of JUnit project from GitHub to local folder `C:\cygwin\usr\local\etc\junit`.

In menu go to _File -> Import..._

In the popup menu open section _Maven_, click on _Existing Maven Projects_ and click on _Next_. In _Import Maven Projects_ specify the project root, and next proceed further with installing maven support plugin in Eclipse.

You have created the Maven project, and now build the project.

In the Package Explorer click on _pom.xml_. In the menu _Run -> Run As -> 2 Maven build..._ open the popup _Edit Configuration_ and specify the build phase _clean install_ in section _Goals_. Click on _Run_ and build the project.

#### How to install and build the Maven project in IntelliJ IDEA

In IDEA menu create a new project _File -> New Project..._.

Select _Create Java project from existing sources_, then click on Next and specify _Project file location_.

On the right-hand side is the _Maven Projects_ tab. Click on + and add _pom.xml_ into the project. Then click on the icon _Maven Settings_, and set _Maven home directory_ as the location of extracted Maven archive on your system. Click on the green triangle and launch the build.

See the IntelliJ IDEA Web help
[http://www.jetbrains.com/idea/webhelp/maven-2.html](http://www.jetbrains.com/idea/webhelp/maven-2.html)

#### How to install the Maven project with documentation
Use the profile `generate-docs` to build _sources.jar_ and _javadoc.jar_. Building Maven site is not yeat supported.

Example: `mvn -Pgenerate-docs install`

#### How to activate and deactivate Maven profiles in Integrated Development Environments:

In _Eclipse_, from the main menu navigate to Run -> Run As -> 2 Maven build..., open the popup _Edit Configuration_ and specify the profiles.

Follow this link for _IntelliJ IDEA_: [http://www.jetbrains.com/idea/webhelp/activating-and-deactivating-maven-profiles.html](http://www.jetbrains.com/idea/webhelp/activating-and-deactivating-maven-profiles.html)


# Miscellaneous


### [Pull request #776:](https://github.com/junit-team/junit4/pull/776) Add support for [Travis CI](http://travis-ci.org)

Travis CI is a free CI server for public Github repositories. Every pull request is run by Travis CI and Github's web interface shows the CI result for each pull request. Every user can use Travis CI for testing her branches, too.

### [Pull request #921:](https://github.com/junit-team/junit4/pull/921) Apply Google Code Style

JUnit is now using the well documented [Google Code Style](http://google-styleguide.googlecode.com/svn/trunk/javaguide.html)

### [Pull request #939](https://github.com/junit-team/junit4/pull/939) Renamed license file

While using JUnit in Android apps, if any other referenced library has a file named `LICENSE.txt`, the APK generation failed with the following error -

`Error generating final archive: Found duplicate file for APK: LICENSE.txt`

To avoid this, the license file has been renamed to `LICENSE-junit.txt` 


### [Pull request #962:](https://github.com/junit-team/junit4/pull/962) Do not include thread start time in test timeout measurements.

The time it takes to start a thread can be surprisingly large.
Especially in virtualized cloud environments where noisy neighbours.
This change reduces the probability of non-deterministic failures of
tests with timeouts (@Test(timeout=…)) by not beginning the timeout
clock until we have observed the starting of the task thread – the
thread that runs the actual test. This should make tests with small
timeout values more reliable in general, and especially in cloud CI
environments.

# Fixes to issues introduced in JUnit 4.12

The following section lists fixes to problems introduced in the first
release candidates for JUnit 4.12. You can ignore this section if you are
trying to understand the changes between 4.11 and 4.12.

### [Pull request #961:](https://github.com/junit-team/junit4/pull/961) Restore field names with f prefix.

In order to make the JUnit code more consistent with current coding practices, we changed
a number of field names to not start with the prefix "f". Unfortunately, at least one IDE
referenced a private field via reflection. This change reverts the field names for fields
known to be read via reflection.

### [Pull request #988:](https://github.com/junit-team/junit4/pull/988) Revert "Delete classes that are deprecated for six years."

In [745ca05](https://github.com/junit-team/junit4/commit/745ca05dccf5cc907e43a58142bb8be97da2b78f)
we removed classes that were deprecated for many releases. There was some concern that people
might not expect classes to be removed in a 4.x release. Even though we are not aware of any
problems from the deletion, we decided to add them back.

These classes may be removed in JUnit 5.0 or later.

### [Pull request #989:](https://github.com/junit-team/junit4/pull/989) Add JUnitSystem.exit() back.

In [917a88f](https://github.com/junit-team/junit4/commit/917a88fad06ce108a596a8fdb4607b1a2fbb3f3e)
the exit() method in JUnit was removed. This caused problems for at least one user. Even
though this class is in an internal package, we decided to add it back, and deprecated it.

This method may be removed in JUnit 5.0 or later.

### [Pull request #994:](https://github.com/junit-team/junit4/pull/994) [Pull request #1000:](https://github.com/junit-team/junit4/pull/1000) Ensure serialization compatibility where possible.

JUnit 4.12 RC1 introduced serilization incompatibilities with some of the classes. For example,
these pre-release versions of JUnit could not read instances of `Result` that were serialized
in JUnit 4.11 and earlier. These changes fix that problem.

