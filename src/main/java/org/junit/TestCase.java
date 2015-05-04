package org.junit;
 
/**
 * Used with {@link org.junit.Test} this represents a set of data values that are inputs to a test method
 * which takes parameters. The values are provided as an array of strings. Example:
 * <pre>
 *      &#064;TestCase({"true", "3.14", "Hello word!"})
 * </pre>
 */
public @interface TestCase {
    String[] value();
}