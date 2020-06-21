## Summary of changes in version 4.13

# Assertions

### [Pull request #1054:](https://github.com/junit-team/junit/pull/1054) Improved error message for `assertArrayEquals` when multi-dimensional arrays have different lengths

Previously, JUnit's assertion error message would indicate only that some array lengths _x_ and _y_ were unequal, without indicating whether this pertained to the outer array or some nested array. Now, in case of a length mismatch between two nested arrays, JUnit will tell at which indices they reside.

### [Pull request #1154](https://github.com/junit-team/junit/pull/1154) and [#1504](https://github.com/junit-team/junit/pull/1504) Add `assertThrows`

The `Assert` class now includes methods that can assert that a given function call (specified, for instance, as a lambda expression or method reference) results in a particular type of exception being thrown. In addition it returns the exception that was thrown, so that further assertions can be made (e.g. to verify that the message and cause are correct).

### [Pull request #1300:](https://github.com/junit-team/junit/pull/1300) Show contents of actual array when array lengths differ

Previously, when comparing two arrays which differ in length, `assertArrayEquals()` would only report that they differ in length. Now, it does the usual array comparison even when arrays differ in length, producing a failure message which combines the difference in length and the first difference in content. Where the content is another array, it is described by its type and length.

### [Pull request #1315:](https://github.com/junit-team/junit4/pull/1315) `assertArrayEquals` shouldn't throw an NPE when test suites are compiled/run across versions of junit

A redundant field, `fCause`, was removed on v4.12, and was seemingly harmless because `Throwable#initCause()` could directly initialize `cause` in the constructor. Unfortunately, this backwards incompatible change got aggravated when a test class, compiled with the latest (4.12+), ran with an older version that depended on fCause when building the assertion message<sup>[1](#1315-f1)</sup>.

This change adds back `fCause`, and overrides `getCause()` to handle forward compatibility<sup>[2](#1315-f2)</sup>.

To ensure serializability of further changes in `ArrayAssertionFailure` (until excising these fields by a major rev), a unit test now runs against v4.11, v4.12 failures, asserting around `#toString/getCause()`.

<a name="1315-f1">[1]</a> [Issue #1178](https://github.com/junit-team/junit4/issues/1178) details a particular case where gradle v2.2 is packaged with junit v4.11 and is used on running a test, generating test reports, despite specifying a particular version of junit (users would specify v4.12, or v4.+) in the test compile dependencies).

<a name="1315-f2">[2]</a> [Case](https://github.com/junit-team/junit4/pull/1315#issuecomment-222905229) if the test class is compiled with <= v4.11, where only `fCause` is initialized and not `Throwable#cause`, it can now fallback to the field, `fCause`, when building the message.

### [Pull request #1150:](https://github.com/junit-team/junit4/pull/1150) Deprecate `Assert#assertThat`

The method `assertThat` is used for writing assertions with Hamcrest. Hamcrest is an independent assertion library and contains an own `assertThat` method in the class `org.hamcrest.MatcherAssert`. It is available both in the old Hamcrest 1.3 release and in the current Hamcrest 2.1. Therefore the JUnit team recommends to use Hamcrest's own `assertThat` directly.

# Test Runners

### [Pull request #1037:](https://github.com/junit-team/junit/pull/1037) `BlockJUnit4ClassRunner#createTest` now accepts `FrameworkMethod`

Subclasses of `BlockJUnit4ClassRunner` can now produce a custom test object based on the `FrameworkMethod` test being executed by implementing the new `createTest(FrameworkMethod)` method. The default implementation calls the existing `createTest()` method.

### [Pull request #1082](https://github.com/junit-team/junit/pull/1082): Ensure exceptions from `BlockJUnit4ClassRunner.methodBlock()` don't result in unrooted tests

The introduction of the `runLeaf()` method in `BlockJUnit4ClassRunner` in JUnit 4.9 introduced a regression with regard to exception handling. Specifically, in JUnit 4.9 through 4.12 the invocation of `methodBlock()` is no longer executed within a try-catch block as was the case in previous versions of JUnit. Custom modifications to `methodBlock()` or the methods it invokes may in fact throw exceptions, and such exceptions cause the current test execution to abort immediately. As a result, the failing test method is unrooted in test reports, and subsequent test methods are never invoked. Furthermore, any `RunListener` registered with JUnit is not notified.

As of JUnit 4.13, the invocation of `methodBlock()` is once again wrapped within a try-catch block. If an exception is _not_ thrown, the resulting `Statement` is passed to `runLeaf()`. If an exception _is_ thrown, it is wrapped in a `Fail` statement which is passed to `runLeaf()`.

### [Pull request #1286](https://github.com/junit-team/junit/pull/1286): Provide better feedback to the user in case of invalid test classes

Only one exception per invalid test class is now thrown, rather than one per validation error.
The message of the exception includes all of the validation errors.

Example:

    org.junit.runners.InvalidTestClassError: Invalid test class 'com.example.MyTest':
      1. Method staticAfterMethod() should not be static
      2. Method staticBeforeMethod() should not be static

is the exception thrown when running the following test class with any kind of `ParentRunner`:

    public class MyTest {

        @Before
        public static void staticBeforeMethod() { .. }

        @After
        public static void staticAfterMethod() { .. }

        @Test
        public void myTest() { .. }
    }

Validation errors for the same test class now count only once in the failure count. Therefore, in the example above, `Result#getFailureCount` will return 1.

### [Pull request #1252](https://github.com/junit-team/junit4/pull/1252): Restore ability use ParentRunner lost in separate class loader

`ParentRunner.getDescription()` now uses the class instance of the test class to create the description
(previously the class instance was loaded using the current classloader).

### [Pull request #1377](https://github.com/junit-team/junit4/pull/1377): Description produced by Request.classes() shouldn't be null

When obtaining a `Runner` via [Request.classes(Class<?>... classes)](http://junit.org/junit4/javadoc/4.12/org/junit/runner/Request.html#classes(java.lang.Class...)), that Runner's `Description` will now print "classes" for the root item. This replaces the misleading output of String "null".

### [Issue #1290](https://github.com/junit-team/junit4/issues/1290): Tests expecting AssumptionViolatedException are now correctly marked as passed

    @Test(expected = AssumptionViolatedException.class)
    public void shouldThrowAssumptionViolatedException() {
        throw new AssumptionViolatedException("expected");
    }

This test would previously be marked as skipped; now will be marked as passed.


### [Pull request #1465](https://github.com/junit-team/junit/pull/1465): Provide helpful message if parameter cannot be set.

JUnit throws an exception with a message like

    Cannot set parameter 'name'. Ensure that the the field 'name' is public.

if a field of a parameterized test is annotated `@Parameter`, but its visibility is not public. Before an IllegalAccessException was thrown with a message like "Class ... can not access a member of class X with modifiers private".


### [Issue #1329](https://github.com/junit-team/junit4/issus/1329): Support assumptions in `@Parameters` method

No test is run when an assumption in the `@Parameters` method fails. The test result for this test class contains one assumption failure and run count is zero.


### [Pull request #1449](https://github.com/junit-team/junit/pull/1449): Parameterized runner reuses TestClass instance

Reduce memory consumption of parameterized tests by not creating a new instance of `TestClass` for every test.

### [Pull request #1130](https://github.com/junit-team/junit/pull/1130): Add Ordering, Orderable and @OrderWith

Test classes can now be annotated with `@OrderWith` to specify that the tests should execute in a particular
order. All runners extending `ParentRunner` support `@OrderWith`. Runners can also be ordered using
`Request.orderWith(Ordering)`

Classes annotated with `@RunWith(Suite.class)` can also be ordered with `@OrderWith`. Note that if this is done, nested classes annotated with `@FixMethodOrder` will not be reordered (i.e. the `@FixMethodOrder` annotation is
always respected). Having a test class annotated with both `@OrderWith` and `@FixMethodOrder` will result in a
validation error (see
[pull request #1638](https://github.com/junit-team/junit4/pull/1638)).

### [Pull request #1408](https://github.com/junit-team/junit/pull/1408): Suites don't have to be public

Classes annotated with `@RunWith(Suite.class)` do not need to be public. This fixes a regression bug in JUnit 4.12. Suites didn't had to be public before 4.12.

### [Pull request #1638](https://github.com/junit-team/junit4/pull/1638): Never reorder classes annotated with @FixMethodOrder

Changing the order of a test run using `Request.sortWith()` no longer changes the order of test classes annotated
with `@FixMethodOrder`. The same holds true when you reorder tests with `Request.orderWith()` (`orderWith()` 
was introduced in [Pull request #1130](https://github.com/junit-team/junit/pull/1130)).

This was done because usually `@FixMethodOrder` is added to a class because the tests in the class they only pass
if run in a specific order. 

Test suites annotated with `@OrderWith` will also respect the `@FixMethodOrder` annotation.

Having a test class annotated with both `@OrderWith` and `@FixMethodOrder` will result in a validation error.

# Rules

### [Pull request #1044:](https://github.com/junit-team/junit/pull/1044) Strict verification of resource deletion in `TemporaryFolder` rule

Previously `TemporaryFolder` rule did not fail the test if some temporary resources could not be deleted. With this change a new `assuredDeletion` parameter is introduced which will fail the test with an `AssertionError`, if resource deletion fails. The default behavior of `TemporaryFolder` is unchanged.

This feature must be enabled by creating a `TemporaryFolder` using the `TemporaryFolder.builder()` method:
```java
@Rule public TemporaryFolder folder = TemporaryFolder.builder().assureDeletion().build();
```

### [Issue #1100:](https://github.com/junit-team/junit/issues/1110) StopWatch does not need to be abstract.

Previously `StopWatch` was an abstract class, which means it cannot be used without extending it or using an anonymous class. The abstract modifier has been removed and StopWatch can be used easily now.

### [Issue #1157:](https://github.com/junit-team/junit/issues/1157) TestName: Make 'name' field volatile

The `name` field in the `TestName` rule was updated to be volatile. This should ensure that the name
is published even when tests are running in parallel.

### [Issue #1223:](https://github.com/junit-team/junit/issues/1223) TemporaryFolder doesn't work for parallel test execution in several JVMs

Previously `TemporaryFolder` rule silently succeeded if it failed to create a fresh temporary directory. With this change it will notice the failure, retry with a new name, and ultimately throw an `IOException` if all such attempts fail.

### [Pull request #1305:](https://github.com/junit-team/junit/pull/1305) Add `ErrorCollector.checkThrows`

The `ErrorCollector` class now has a `checkThrows` method that can assert that a given function call (specified, for instance, as a lambda expression or method reference) results in a particular type of exception being thrown.

### [Issue #1303:](https://github.com/junit-team/junit/issues/1303) Prevent following symbolic links when deleting temporary directories

Previously, `TemporaryFolder` would follow symbolic links; now it just deletes them.

Following symbolic links when removing files can lead to the removal of files outside the directory structure rooted in the temporary folder, and it can lead to unbounded recursion if a symbolic link points to a directory where the link is directly reachable from.

### [Issue #1295:](https://github.com/junit-team/junit/issues/1295) Javadoc for RuleChain contains errors

Removed error from RuleChain Javadoc and clarified how it works with existing rules. Removed `static` modifier, added missing closing parenthesis of method calls and added clarification.

### [Pull request #1313](https://github.com/junit-team/junit4/pull/1313): `RuleChain.around()` rejects null arguments
`RuleChain.around()` now implements a fail-fast strategy which also allows for better feedback to the final user, as the stacktrace will point to the exact line where the null rule is declared.

### [Pull request #1397](https://github.com/junit-team/junit4/pull/1397): Change generics on `ExpectedException.expectCause()`
The signature of `ExpectedException.expectCause()` would not allow the caller to pass in a `Matcher<Object>` (which is returned by `CoreMatchers.notNullValue()`). This was fixed by changing the method to take in a
`Matcher<?>` (ideally, the method should take in `Matcher<? super Throwable>` but there was concern that
changing the parameter type to `Matcher<? super Throwable>` would break some callers).  

### [Pull request #1443](https://github.com/junit-team/junit4/pull/1443): `ExpectedException.isAnyExceptionExpected()` is now public
The method `ExpectedException.isAnyExceptionExpected()` returns `true` if there is at least one expectation present for the `ExpectedException` rule.

### [Pull request #1395](https://github.com/junit-team/junit4/pull/1395): Wrap assumption violations in ErrorCollector
Both `ErrorCollector.addError()` and `ErrorCollector.checkSucceeds()` now wrap `AssumptionViolatedException`.
In addition, `ErrorCollector.addError()` will throw a `NullPointerException` if you pass in a `null` value.

### [Pull request #1402](https://github.com/junit-team/junit4/pull/1402): TemporaryFolder.newFolder(String) supports paths with slashes
There was a regression in JUnit 4.12 where `TemporaryFolder.newFolder(String)` no longer supported passing
in strings with separator characters. This has been fixed. he overload of newFolder() that
supports passing in multiple strings still does not allow path separators.

### [Pull requests #1406 (part 1)](https://github.com/junit-team/junit4/pull/1406) and [#1568](https://github.com/junit-team/junit4/pull/1568): Improve error message when TemporaryFolder.newFolder(String) fails
When `newFolder(String path)` was not able to create the folder then it always failed with the error message "a folder with the name '`<path>`' already exists" although the reason for the failure could be something else. This message is now only used if the folder really exists. The message is "a file with the path '`<path>`' exists" if the whole path or a part of the path points to a file. In all other cases it fails now with the message "could not create a folder with the path '`<path>`'"

### [Pull request #1406 (part 2)](https://github.com/junit-team/junit4/pull/1406): TemporaryFolder.newFolder(String...) supports path separator
You can now pass paths with path separators to `TemporaryFolder.newFolder(String...)`. E.g.

    tempFolder.newFolder("temp1", "temp2", "temp3/temp4")

It creates a folder `temp1/temp2/temp3/temp4`.


### [Pull request #1406 (part 3)](https://github.com/junit-team/junit4/pull/1406): TemporaryFolder.newFolder(String...) fails for empty array

When you call

    tempFolder.newFolder(new String[])

then it throws an `IllegalArgumentException` instead of returning an already existing folder.


### [Pull request #1335](https://github.com/junit-team/junit4/pull/1335): Fix ExternalResource: the test failure could be lost
When both the test failed and closing the resource failed, only the exception coming from the `after()` method was propagated, as per semantics of the try-finally (see also http://docs.oracle.com/javase/specs/jls/se8/html/jls-14.html#jls-14.20.2).

The new behavior is compatible with @After method semantics, as implemented in [RunAfters](https://github.com/junit-team/junit4/blob/HEAD/src/main/java/org/junit/internal/runners/statements/RunAfters.java).

### [Pull request #1435](https://github.com/junit-team/junit4/pull/1435): @BeforeParam/@AfterParam method annotations for Parameterized tests.
This allows having preparation and/or cleanup in tests for specific parameter values.

### [Pull request #1460](https://github.com/junit-team/junit4/pull/1460): Handle assumption violations in the @Parameters method for Parameterized tests.
This allows skipping the whole test class when its assumptions are not met.

### [Pull request #1445](https://github.com/junit-team/junit4/pull/1445) and [Pull request #1335](https://github.com/junit-team/junit4/pull/1501): Declarative ordering of rules.
The order in which rules are executed is specified by the annotation attribute: `@Rule(order = N)`, deprecating `RuleChain`. This may be used for avoiding some common pitfalls with `TestWatcher, `ErrorCollector` and `ExpectedException` for example. The Javadoc of `TestWatcher` was retrofitted accordingly.

### [Pull request #1517](https://github.com/junit-team/junit4/pull/1517): Timeout rule destroys its ThreadGroups at the end
The `ThreadGroup` created for handling the timeout of tests is now destroyed, so the main thread group no longer keeps a reference to all timeout groups created during the tests. This caused the `threadGroup` to remain in memory, and all of its context along with it.

### [Pull request #1633](https://github.com/junit-team/junit4/pull/1633): Deprecate ExpectedException.none()
The method Assert.assertThrows provides a nicer way for verifying exceptions. In addition the use of ExpectedException is error-prone when used with other rules like TestWatcher because the order of rules is important in that case.

### [Pull request #1413](https://github.com/junit-team/junit4/pull/1413): Ignore bridge methods when scanning for annotated methods

In a setup with a class hierarchy for test classes the order of rules (from methods), before methods, after methods and others depends on the class that contains these methods. Compilers can add bridge methods to child classes and therefore the order of the aforementioned methods can change in older JUnit releases. This is now fixed because bridge methods are ignored when scanning for annotated methods.

### [Pull request #1612](https://github.com/junit-team/junit4/pull/1612): Make @ValidateWith only applicable to annotation types

`@Target(ANNOTATION_TYPE)` has been added to `@ValidateWith` since it's only designed to be applied to another annotation.

# Run Listener

### [Pull request #1118:](https://github.com/junit-team/junit4/pull/1118) Add suite start/finish events to listener

The `RunListener` class now has `fireTestSuiteStarted` and `fireTestSuiteFinished` methods that notify when test suites are about to be started/finished.


# Exception Testing

### [Pull request #1359:](https://github.com/junit-team/junit4/pull/1359) Fixes how `MultipleFailureException` stack traces are printed

Previously, calling `MultipleFailureException.printStackTrace()` only printed the stack trace for the `MultipleFailureException` itself. After this change, the stack trace for each exception caught in the `MultipleFailureException` is printed.

### [Pull request #1376:](https://github.com/junit-team/junit4/pull/1376) Initializing MultipleFailureException with an empty list will now fail the test

Previously, initializing `MultipleFailureException` with an empty list of contained Exceptions and throwing it in a test case wouldn't actually fail the test. Now an `IllegalArgumentException` will be raised in this situation and thus also fail the test.

### [Pull request #1371:](https://github.com/junit-team/junit4/pull/1371) Update MultipleFailureException.assertEmpty() to wrap assumption failure

`MultipleFailureException` will now wrap `MultipleFailureException` with `TestCouldNotBeSkippedException`. Previously, if you passed `MultipleFailureException` one `MultipleFailureException`--and no other exceptions--
then the test would be skipped, otherwise it would fail. With the new behavior, it will always fail.

### [Issue #1290:](https://github.com/junit-team/junit4/issues/1290) `@Test(expected = AssumptionViolatedException.class)` passes for AssumptionViolatedException

Tests annotated with `@test(expected = AssumptionViolatedException.class)`
which throw AssumptionViolatedException had been marked as skipped. Now the are marked as successful tests.


# JUnit 3 Changes

### [Pull request #1227:](https://github.com/junit-team/junit/pull/1227) Behave better if the `SecurityManager` denies access to `junit.properties`

Previously, running tests with a `SecurityManager` would cause the test runner itself to throw an `AccessControlException` if the security policy didn't want it reading from `~/junit.properties`. This will now be treated the same as if the file does not exist.

# Misc

### [Pull request #1571:](https://github.com/junit-team/junit4/pull/1571) Set "junit" as "Automatic-Module-Name"

For existing releases of JUnit the `Automatic-Module-Name` was derived from the name of the jar. In most cases it is already the name "junit". JUnit 4.13 explicitly sets the module name to "junit" so that it is independent from the jar's name.


### [Pull request #1028:](https://github.com/junit-team/junit4/pull/1028) Trim stack trace

JUnit's command-line runner (`JUnitCore`) prints smaller stack traces. It skips all stack trace elements that come before the test method so that it starts at the test method. E.g. the output for the example from [Getting started](https://github.com/junit-team/junit4/wiki/Getting-started) page is now

    .E
    Time: 0,006
    There was 1 failure:
    1) evaluatesExpression(CalculatorTest)
    java.lang.AssertionError: expected:<6> but was:<-6>
    	at org.junit.Assert.fail(Assert.java:89)
    	at org.junit.Assert.failNotEquals(Assert.java:835)
    	at org.junit.Assert.assertEquals(Assert.java:647)
    	at org.junit.Assert.assertEquals(Assert.java:633)
    	at CalculatorTest.evaluatesExpression(CalculatorTest.java:9)

    FAILURES!!!
    Tests run: 1,  Failures: 1


### [Pull request #1403:](https://github.com/junit-team/junit4/pull/1403) Restore CategoryFilter constructor

The constructor `CategoryFilter(Class<?> includedCategory, Class<?> excludedCategory)` has been removed in JUnit 4.12. It is now available again.


### [Pull request #1530:](https://github.com/junit-team/junit4/pull/1530) Add Result#getAssumptionFailureCount

Add method `getAssumptionFailureCount()` to `Result` which returns the number of assumption failures.

### [Pull request #1292:](https://github.com/junit-team/junit4/pull/1292) Fix ResultMatchers#hasFailureContaining
 
`ResultMatchers.hasFailureContaining()` should return `false` when the given `PrintableResult` has no failures.

### [Pull request #1380:](https://github.com/junit-team/junit4/pull/1380) Fix Assume#assumeNotNull

`Assume.assumeNotNull` should throw AssumptionViolatedException when called with a `null` array.

### [Pull request #1557:](https://github.com/junit-team/junit4/pull/1380) MaxCore always closes stream of history file

MaxCore didn't close the output stream of the history file when write failed. Now it does.

### Signing

The 4.13 release is signed with a new key (id 5EC61B51):
https://github.com/junit-team/junit4/blob/8c0df64ff17fead54c304a8b189da839084925c2/KEYS
