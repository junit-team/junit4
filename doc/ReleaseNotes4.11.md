## Summary of changes in version 4.11

### Matchers: Upgrade to Hamcrest 1.3

JUnit now uses the latest version of Hamcrest. Thus, you can use all the available matchers and benefit from an improved `assertThat` which will now print the mismatch description from the matcher when an assertion fails.

#### Example

```java
assertThat(Long.valueOf(1), instanceOf(Integer.class));
```

Old error message:

    Expected: an instance of java.lang.Integer
         got: <1L>

New error message:

    Expected: an instance of java.lang.Integer
         but: <1L> is a java.lang.Long

Hamcrest's new `FeatureMatcher` makes writing custom matchers that make use of custom mismatch descriptions quite easy:

```java
@Test
public void featureMatcher() throws Exception {
    assertThat("Hello World!", length(is(0)));
}

private Matcher<String> length(Matcher<? super Integer> matcher) {
    return new FeatureMatcher<String, Integer>(matcher, "a String of length that", "length") {
        @Override
        protected Integer featureValueOf(String actual) {
            return actual.length();
        }
    };
}
```

Running this test will return the following failure message:

    Expected: a String of length that is <0>
         but: length was <12>


Most of the matchers in `JUnitMatchers` have been deprecated. Please use `org.hamcrest.CoreMatchers` directly.

### Parameterized Tests

In order to easily identify the individual test cases in a Parameterized test, you may provide a name using the `@Parameters` annotation. This name is allowed to contain placeholders that are replaced at runtime:

* `{index}`: the current parameter index
* `{0}`, `{1}`, …: the first, second, and so on, parameter value

#### Example

```java
@RunWith(Parameterized.class)
public class FibonacciTest {
    
    @Parameters(name = "{index}: fib({0})={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },
    		{ 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
    }
    
    private int input;
    private int expected;
    
    public FibonacciTest(int input, int expected) {
        this.input = input;
    	this.expected = expected;
    }
    
    @Test
    public void test() {
        assertEquals(expected, Fibonacci.compute(input));
    }
}
```

In the example given above, the `Parameterized` runner creates names like `[1: fib(3)=2]`. If you don't specify a name, the current parameter index will be used by default.

### Test execution order

By design, JUnit does not specify the execution order of test method invocations. Until now, the methods were simply invoked in the order returned by the reflection API. However, using the JVM order is unwise since the Java platform does not specify any particular order, and in fact JDK 7 returns a more or less random order. Of course, well-written test code would not assume any order, but some does, and a predictable failure is better than a random failure on certain platforms.

From now on, JUnit will by default use a deterministic, but not predictable, order (`MethodSorters.DEFAULT`). To change the test execution order simply annotate your test class using `@FixMethodOrder` and specify one of the available `MethodSorters`:

* `@FixMethodOrder(MethodSorters.JVM)`: Leaves the test methods in the order returned by the JVM. This order may vary from run to run.

* `@FixMethodOrder(MethodSorters.NAME_ASCENDING)`: Sorts the test methods by method name, in lexicographic order.

### Maven artifacts

Up until now there were two Maven artifacts for JUnit: `junit:junit-dep` and `junit:junit`. From a Maven point-of-view only the former made sense because it did not contain the Hamcrest classes but declared a dependency to the Hamcrest Maven artifact. The latter included the Hamcrest classes which was very un-Maven-like.

From this release on, you should use `junit:junit` which will be what `junit:junit-dep` used to. If you still reference `junit:junit-dep`, Maven will automatically relocate you to the new `junit:junit` and issue a warning for you to fix.

### Rules

A number of improvements have been made to Rules:

* `MethodRule` is no longer deprecated.
* Both `@Rule` and `@ClassRule` can now be used on methods that return a `TestRule`.
* `ExpectedException` now always prints the stacktrace of the actual exception in case of failure.
* A parent folder can be specified for `TemporaryFolder`. In addition, the `newFile`/`newFolder` methods will now fail when the file or folder could not be created.
* `TestWatcher` has a new template method called `skipped` that is invoked when a test is skipped due to a failed assumption.

### Improvements to Assert and Assume

* `assertNotEquals` has been added to `Assert`.
* `assertEquals` has been overloaded in order to check whether two floats are equal given a certain float delta.
* Most methods in `Assume` now allow to pass a custom message.

