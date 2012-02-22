// Generated source.
package org.hamcrest;

public class CoreMatchers {

  /**
   * Decorates another Matcher, retaining the behavior but allowing tests
   * to be slightly more expressive.
   * 
   * eg. assertThat(cheese, equalTo(smelly))
   * vs assertThat(cheese, is(equalTo(smelly)))
   */
  public static <T> org.hamcrest.Matcher<T> is(org.hamcrest.Matcher<T> matcher) {
    return org.hamcrest.core.Is.is(matcher);
  }

  /**
   * This is a shortcut to the frequently used is(equalTo(x)).
   * 
   * eg. assertThat(cheese, is(equalTo(smelly)))
   * vs assertThat(cheese, is(smelly))
   */
  public static <T> org.hamcrest.Matcher<T> is(T value) {
    return org.hamcrest.core.Is.is(value);
  }

  /**
   * This is a shortcut to the frequently used is(instanceOf(SomeClass.class)).
   * 
   * eg. assertThat(cheese, is(instanceOf(Cheddar.class)))
   * vs assertThat(cheese, is(Cheddar.class))
   */
  public static org.hamcrest.Matcher<java.lang.Object> is(java.lang.Class<?> type) {
    return org.hamcrest.core.Is.is(type);
  }

  /**
   * Inverts the rule.
   */
  public static <T> org.hamcrest.Matcher<T> not(org.hamcrest.Matcher<T> matcher) {
    return org.hamcrest.core.IsNot.not(matcher);
  }

  /**
   * This is a shortcut to the frequently used not(equalTo(x)).
   * 
   * eg. assertThat(cheese, is(not(equalTo(smelly))))
   * vs assertThat(cheese, is(not(smelly)))
   */
  public static <T> org.hamcrest.Matcher<T> not(T value) {
    return org.hamcrest.core.IsNot.not(value);
  }

  /**
   * Is the value equal to another value, as tested by the
   * {@link java.lang.Object#equals} invokedMethod?
   */
  public static <T> org.hamcrest.Matcher<T> equalTo(T operand) {
    return org.hamcrest.core.IsEqual.equalTo(operand);
  }

  /**
   * Is the value an instance of a particular type?
   */
  public static org.hamcrest.Matcher<java.lang.Object> instanceOf(java.lang.Class<?> type) {
    return org.hamcrest.core.IsInstanceOf.instanceOf(type);
  }

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> org.hamcrest.Matcher<T> allOf(org.hamcrest.Matcher<? extends T>... matchers) {
    return org.hamcrest.core.AllOf.allOf(matchers);
  }

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> org.hamcrest.Matcher<T> allOf(java.lang.Iterable<org.hamcrest.Matcher<? extends T>> matchers) {
    return org.hamcrest.core.AllOf.allOf(matchers);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> org.hamcrest.Matcher<T> anyOf(org.hamcrest.Matcher<? extends T>... matchers) {
    return org.hamcrest.core.AnyOf.anyOf(matchers);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> org.hamcrest.Matcher<T> anyOf(java.lang.Iterable<org.hamcrest.Matcher<? extends T>> matchers) {
    return org.hamcrest.core.AnyOf.anyOf(matchers);
  }

  /**
   * Creates a new instance of IsSame
   * 
   * @param object The predicate evaluates to true only when the argument is
   * this object.
   */
  public static <T> org.hamcrest.Matcher<T> sameInstance(T object) {
    return org.hamcrest.core.IsSame.sameInstance(object);
  }

  /**
   * This matcher always evaluates to true.
   */
  public static <T> org.hamcrest.Matcher<T> anything() {
    return org.hamcrest.core.IsAnything.anything();
  }

  /**
   * This matcher always evaluates to true.
   * 
   * @param description A meaningful string used when describing itself.
   */
  public static <T> org.hamcrest.Matcher<T> anything(java.lang.String description) {
    return org.hamcrest.core.IsAnything.anything(description);
  }

  /**
   * This matcher always evaluates to true. With type inference.
   */
  public static <T> org.hamcrest.Matcher<T> any(java.lang.Class<T> type) {
    return org.hamcrest.core.IsAnything.any(type);
  }

  /**
   * Matches if value is null.
   */
  public static <T> org.hamcrest.Matcher<T> nullValue() {
    return org.hamcrest.core.IsNull.nullValue();
  }

  /**
   * Matches if value is null. With type inference.
   */
  public static <T> org.hamcrest.Matcher<T> nullValue(java.lang.Class<T> type) {
    return org.hamcrest.core.IsNull.nullValue(type);
  }

  /**
   * Matches if value is not null.
   */
  public static <T> org.hamcrest.Matcher<T> notNullValue() {
    return org.hamcrest.core.IsNull.notNullValue();
  }

  /**
   * Matches if value is not null. With type inference.
   */
  public static <T> org.hamcrest.Matcher<T> notNullValue(java.lang.Class<T> type) {
    return org.hamcrest.core.IsNull.notNullValue(type);
  }

  /**
   * Wraps an existing matcher and overrides the description when it fails.
   */
  public static <T> org.hamcrest.Matcher<T> describedAs(java.lang.String description, org.hamcrest.Matcher<T> matcher, java.lang.Object... values) {
    return org.hamcrest.core.DescribedAs.describedAs(description, matcher, values);
  }

}
