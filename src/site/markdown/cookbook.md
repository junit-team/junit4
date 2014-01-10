Title: JUnit Cookbook
Author: Kent Beck, Erich Gamma


Here is a short cookbook showing you the steps you can follow in writing
and organizing your own tests using JUnit.

## Simple Test Case

How do you write testing code?

The simplest way is as an expression in a debugger. You can change debug
expressions without recompiling, and you can wait to decide what to write
until you have seen the running objects. You can also write test expressions
as statements which print to the standard output stream. Both styles of
tests are limited because they require human judgment to analyze their
results. Also, they don't compose nicely- you can only execute one debug
expression at a time and a program with too many print statements causes
the dreaded "Scroll Blindness".

JUnit tests do not require human judgment to interpret, and it is easy
to run many of them at the same time. When you need to test something,
here is what you do:

1.  Annotate a method with `@org.junit.Test`

1. When you want to check a value, import `org.junit.Assert.*` statically, call `assertTrue()` and pass a boolean
that is true if the test succeeds

For example, to test that the sum of two Moneys with the same currency
contains a value which is the sum of the values of the two Moneys, write:

    @Test
    public void simpleAdd() {
        Money m12CHF= new Money(12, "CHF");
        Money m14CHF= new Money(14, "CHF");
        Money expected= new Money(26, "CHF");
        Money result= m12CHF.add(m14CHF);
        assertTrue(expected.equals(result));
    }

If you want to write a test similar to one you have already written, write
a Fixture instead.

## Fixture

What if you have two or more tests that operate on the same or similar
sets of objects?

Tests need to run against the background of a known set of objects.
This set of objects is called a test fixture. When you are writing tests
you will often find that you spend more time writing the code to set up
the fixture than you do in actually testing values.

To some extent, you can make writing the fixture code easier by paying
careful attention to the constructors you write. However, a much bigger
savings comes from sharing fixture code. Often, you will be able to use
the same fixture for several different tests. Each case will send slightly
different messages or parameters to the fixture and will check for different
results.

When you have a common fixture, here is what you do:

1.  Add a field for each part of the fixture
2.  Annotate a method with `@org.junit.Before` and initialize the variables in that method
3.  Annotate a method with `@org.junit.After` to release any permanent resources you allocated in `setUp`

For example, to write several test cases that want to work with different
combinations of 12 Swiss Francs, 14 Swiss Francs, and 28 US Dollars, first
create a fixture:

    public class MoneyTest {
        private Money f12CHF;
        private Money f14CHF;
        private Money f28USD;

        @Before public void setUp() {
            f12CHF= new Money(12, "CHF");
            f14CHF= new Money(14, "CHF");
            f28USD= new Money(28, "USD");
        }
    }

Once you have the Fixture in place, you can write as many Test Cases as
you'd like. Add as many test methods (annotated with `@Test`) as you'd like.

## Running Tests

How do you run your tests and collect their results?

Once you have tests, you'll want to run them. JUnit provides tools
to define the suite to be run and to display its results. To run tests and see the
results on the console, run this from a Java program:

    org.junit.runner.JUnitCore.runClasses(TestClass1.class, ...);

or this from the command line, with both your test class and junit on the classpath:

    java org.junit.runner.JUnitCore TestClass1.class [...other test classes...]

You make your JUnit 4 test classes accessible to a TestRunner designed to work with earlier versions of JUnit,
declare a static method _suite_
that returns a test.

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(Example.class);
    }

## Expected Exceptions

How do you verify that code throws exceptions as expected?

Verifying that code completes normally is only part of programming. Making sure the code
behaves as expected in exceptional situations is part of the craft of programming too. For example:

    new ArrayList<Object>().get(0);

This code should throw an `IndexOutOfBoundsException`. The `@Test` annotation has an optional parameter `expected`
that takes as values subclasses of `Throwable`. If we wanted to verify that `ArrayList` throws the correct exception,
we would write:

    @Test(expected= IndexOutOfBoundsException.class)
    public void empty() {
        new ArrayList<Object>().get(0);
    }

* * *
